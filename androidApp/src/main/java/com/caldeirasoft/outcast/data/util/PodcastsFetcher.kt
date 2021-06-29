/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caldeirasoft.outcast.data.util;

import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.network.rss.ITunesParser
import com.caldeirasoft.outcast.data.util.PodcastsFetcher.Companion.RFC_1123_DATE_TIME
import com.caldeirasoft.outcast.domain.models.rss.channel.ITunesChannelData
import com.caldeirasoft.outcast.domain.models.rss.item.ITunesItemData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import kotlinx.datetime.Instant.Companion.fromEpochSeconds
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.time.chrono.IsoChronology
import java.time.format.*
import java.time.temporal.ChronoField
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import java.time.Instant as jtInstant

/**
 * A class which fetches some selected podcast RSS feeds.
 *
 * @param okHttpClient [OkHttpClient] to use for network requests
 * @param syndFeedInput [SyndFeedInput] to use for parsing RSS feeds.
 * @param ioDispatcher [CoroutineDispatcher] to use for running fetch requests.
 */
class PodcastsFetcher(
    private val okHttpClient: OkHttpClient,
    private val ioDispatcher: CoroutineDispatcher,
) {

    /**
     * It seems that most podcast hosts do not implement HTTP caching appropriately.
     * Instead of fetching data on every app open, we instead allow the use of 'stale'
     * network responses (up to 8 hours).
     */
    private val cacheControl by lazy {
        CacheControl.Builder().maxStale(8, TimeUnit.HOURS).build()
    }

    /**
     * Returns a [Flow] which fetches each podcast feed and emits it in turn.
     *
     * The feeds are fetched concurrently, meaning that the resulting emission order may not
     * match the order of [feedUrls].
     */
    operator fun invoke(
        feedUrl: String,
        currentPodcast: Podcast? = null,
    ): Flow<PodcastRssResponse> = flow {
        emit(fetchPodcast(feedUrl, currentPodcast))
    }


    /**
     * Returns a [Flow] which fetches each podcast feed and emits it in turn.
     *
     * The feeds are fetched concurrently, meaning that the resulting emission order may not
     * match the order of [feedUrls].
     */
    operator fun invoke(feedUrls: List<String>): Flow<PodcastRssResponse> = feedUrls.asFlow()
        // We use flatMapMerge here to achieve concurrent fetching/parsing of the feeds.
        .flatMapMerge { feedUrl ->
            flow { emit(fetchPodcast(feedUrl)) }
        }

    private suspend fun fetchPodcast(url: String, currentPodcast: Podcast? = null): PodcastRssResponse =
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build()
            val response: Response = okHttpClient.newCall(request = request).execute()
            val responseContent = response.body?.string()
            val xmlContent = responseContent.orEmpty()
            val itunesModel: ITunesChannelData = ITunesParser().parse(xmlContent)
            return@withContext itunesModel.toPodcastResponse(url, currentPodcast)
        }

    companion object {
        const val USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0"

        val dow = HashMap<Long, String>().apply {
            put(1L, "Mon")
            put(2L, "Tue")
            put(3L, "Wed")
            put(4L, "Thu")
            put(5L, "Fri")
            put(6L, "Sat")
            put(7L, "Sun")
        }

        val moy = HashMap<Long, String>().apply {
            put(1L, "Jan")
            put(2L, "Feb")
            put(3L, "Mar")
            put(4L, "Apr")
            put(5L, "May")
            put(6L, "Jun")
            put(7L, "Jul")
            put(8L, "Aug")
            put(9L, "Sep")
            put(10L, "Oct")
            put(11L, "Nov")
            put(12L, "Dec")
        }

        /**
         * [DateTimeFormatter.RFC_1123_DATE_TIME] with support for zone ids (e.g. PST).
         */
        val RFC_1123_DATE_TIME = DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .parseLenient()
            .optionalStart()
            .appendText(ChronoField.DAY_OF_WEEK, dow)
            .appendLiteral(", ")
            .optionalEnd()
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, SignStyle.NOT_NEGATIVE)
            .appendLiteral(' ')
            .appendText(ChronoField.MONTH_OF_YEAR, moy)
            .appendLiteral(' ')
            .appendValue(ChronoField.YEAR, 4)  // 2 digit year not handled
            .appendLiteral(' ')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .optionalEnd()
            .appendLiteral(' ')
            .optionalStart()
            .appendZoneText(TextStyle.SHORT) // optionally handle UT/Z/EST/EDT/CST/CDT/MST/MDT/PST/MDT
            .optionalEnd()
            .optionalStart()
            .appendOffset("+HHMM", "GMT")
            .toFormatter().withResolverStyle(ResolverStyle.SMART)
            .withChronology(IsoChronology.INSTANCE)
    }
}

data class PodcastRssResponse(
    val podcast: Podcast,
    val episodes: List<Episode>,
)


/**
 * Map a [ITunesChannelData] instance to our own [Podcast] data class.
 */
private fun ITunesChannelData.toPodcastResponse(
    feedUrl: String,
    currentPodcast: Podcast? = null,
): PodcastRssResponse {
    val releaseDateTime = getReleaseDateTime(this.items?.first())

    val podcast = Podcast(
        feedUrl = feedUrl,
        podcastId = currentPodcast?.podcastId,
        name = title.orEmpty(),
        artistName = author.orEmpty(),
        artistId = currentPodcast?.artistId,
        artistUrl = currentPodcast?.artistUrl,
        url = "",
        genre = currentPodcast?.genre,
        artworkUrl = currentPodcast?.artworkUrl ?: image?.url.orEmpty(),
        artworkDominantColor = currentPodcast?.artworkDominantColor,
        copyright = copyright,
        description = description?.trim()?.trimIndent(),
        podcastWebsiteURL = link,
        releaseDateTime = releaseDateTime,
        trackCount = items?.size?.toLong() ?: 0L,
        updatedAt = releaseDateTime,
        userRating = currentPodcast?.userRating,
        isFollowed = false,
        isComplete = complete ?: false,
        isExplicit = explicit ?: false,
        newFeedUrl = newFeedUrl,
    )

    val episodes = items?.map { it.toEpisode(podcast) }.orEmpty()

    return PodcastRssResponse(podcast, episodes)
}

/**
 * Map a [ITunesItemData] instance to our own [Episode] data class.
 */
private fun ITunesItemData.toEpisode(podcast: Podcast): Episode {
    return Episode(
        guid = guid?.value.orEmpty(),
        name = title.orEmpty(),
        url = "",
        podcastId = podcast.podcastId,
        podcastName = podcast.name,
        artistName = author.orEmpty(),
        artistId = podcast.artistId,
        description = description?.trim()?.trimIndent(),
        feedUrl = podcast.feedUrl,
        releaseDateTime = getReleaseDateTime(this),
        artworkUrl = image ?: podcast.artworkUrl,
        isExplicit = explicit ?: false,
        mediaUrl = enclosure?.url.orEmpty(),
        mediaType = enclosure?.type.orEmpty(),
        duration = duration?.let { parseDurationString(it) } ?: 0,
        podcastEpisodeNumber = episode,
        podcastEpisodeSeason = season,
        podcastEpisodeType = episodeType,
        podcastEpisodeWebsiteUrl = link,
        updatedAt = Clock.System.now().toString(),
        isSaved = false,
    )
}

fun getReleaseDateTime(item: ITunesItemData?): Instant =
    runCatching {
        item?.pubDate
            //?.let { convertZonedDate(it) }
            ?.let {
                fromEpochSeconds(
                    jtInstant.from(
                        RFC_1123_DATE_TIME.parse(
                            it
                        )
                    ).epochSecond
                )
            }
            ?: Instant.DISTANT_PAST
    }.getOrElse {
        Timber.e("Problem during date parsing of %s caused by %s", item?.title.orEmpty(), it.message)
        Clock.System.now()
    }

fun parseDurationString(duration: String): Int {
    val timeParts = duration.split(":".toRegex())
        .toTypedArray()
    val millisecondsParts = duration.split(".")
    val milliseconds = if (millisecondsParts.size > 1) millisecondsParts.last()
        .toLong() else 0L

    return timeParts.reversedArray()
        .mapIndexed { index, part ->
            part.toDouble() * 60.toDouble()
                .pow((index.toDouble()))
        }
        .reduce { sum, element ->
            sum + element
        }
        .toInt()
}