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
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.enums.NewEpisodesAction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.fromEpochSeconds
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import tw.ktrssreader.Reader
import tw.ktrssreader.kotlin.model.channel.ITunesChannelData
import tw.ktrssreader.kotlin.model.item.ITunesItemData
import java.time.format.DateTimeFormatter
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

    suspend fun fetchPodcast(url: String, currentPodcast: Podcast? = null): PodcastRssResponse =
        Reader.coRead<ITunesChannelData>(url).toPodcastResponse(url, currentPodcast)

}

data class PodcastRssResponse(
    val podcast: Podcast,
    val episodes: List<Episode>,
)


/**
 * Map a KtRssReader [ITunesChannelData] instance to our own [Podcast] data class.
 */
private fun ITunesChannelData.toPodcastResponse(
    feedUrl: String,
    currentPodcast: Podcast? = null,
): PodcastRssResponse {
    val releaseDateTime = items
        ?.first()
        ?.pubDate
        ?.let { fromEpochSeconds(jtInstant.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(it)).epochSecond) }
        ?: Instant.DISTANT_PAST
    val category = categories
        ?.mapNotNull { it.name }
        ?.map { Category.fromName(it) }
        ?.firstOrNull()
    val podcast = Podcast(
        feedUrl = feedUrl,
        podcastId = currentPodcast?.podcastId,
        name = title.orEmpty(),
        artistName = author.orEmpty(),
        artistId = currentPodcast?.artistId,
        artistUrl = currentPodcast?.artistUrl,
        url = "",
        category = category,
        artworkUrl = currentPodcast?.artworkUrl ?: image?.url.orEmpty(),
        artworkDominantColor = currentPodcast?.artworkDominantColor,
        copyright = copyright,
        description = description?.trim()?.trimIndent(),
        podcastWebsiteURL = link,
        releaseDateTime = releaseDateTime,
        trackCount = 0,
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
 * Map a KtRssReader [ITunesItemData] instance to our own [Episode] data class.
 */
private fun ITunesItemData.toEpisode(podcast: Podcast): Episode {
    val releaseDateTime = pubDate
        ?.let { fromEpochSeconds(jtInstant.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(it)).epochSecond) }
        ?: Instant.DISTANT_PAST
    return Episode(
        guid = guid.toString(),
        name = title.orEmpty(),
        url = "",
        podcastId = podcast.podcastId,
        podcastName = podcast.name,
        artistName = author.orEmpty(),
        artistId = podcast.artistId,
        description = description?.trim()?.trimIndent(),
        feedUrl = podcast.feedUrl,
        releaseDateTime = releaseDateTime,
        artworkUrl = image ?: podcast.artworkUrl,
        isExplicit = explicit ?: false,
        mediaUrl = enclosure?.url.orEmpty(),
        mediaType = enclosure?.type.orEmpty(),
        duration = duration?.let { parseDurationString(it) } ?: 0,
        podcastEpisodeNumber = episode,
        podcastEpisodeSeason = season,
        podcastEpisodeType = episodeType,
        podcastEpisodeWebsiteUrl = link,
        updatedAt = Clock.System.now(),
        isPlayed = false,
        playedAt = Instant.DISTANT_PAST,
        isFavorite = false,
        playbackPosition = null,
    )
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