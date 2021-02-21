package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.caldeirasoft.outcast.domain.models.Podcast
import com.caldeirasoft.outcast.domain.models.PodcastSummary
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.datetime.Instant

class PodcastRepository(val database: Database)
{
    fun fetchSubscribedPodcasts(): Flow<List<PodcastSummary>> =
        database.podcastQueries
            .selectAll(mapper = { podcastId: Long,
                                  name: String,
                                  artwork: Artwork?
                ->
                PodcastSummary(podcastId, name, artwork)
            })
            .asFlow()
            .mapToList()

    fun getPodcast(id: Long): Flow<Podcast> =
        database.podcastQueries
            .selectPodcastById(id, mapper = { podcastId: Long,
                                                     name: String,
                                                     url: String,
                                                     artistName: String,
                                                     artistId: Long?,
                                                     artistUrl: String?,
                                                     description: String?,
                                                     feedUrl: String,
                                                     releaseDateTime: Instant,
                                                     artwork: Artwork?,
                                                     trackCount: Long,
                                                     podcastWebsiteURL: String?,
                                                     copyright: String?,
                                                     contentAdvisoryRating: String?,
                                                     userRating: Double?,
                                                     genreId: Int?,
                                                     isSubscribed: Boolean,
                                                     newEpisodeAction: NewEpisodesAction,
                                                     updatedAt: Instant
                ->
                Podcast(
                    podcastId,
                    name,
                    url,
                    artistName,
                    artistId,
                    artistUrl,
                    description,
                    feedUrl,
                    releaseDateTime,
                    artwork,
                    trackCount,
                    podcastWebsiteURL,
                    copyright,
                    contentAdvisoryRating,
                    userRating,
                    genreId,
                    isSubscribed,
                    newEpisodeAction,
                    updatedAt
                )
            })
            .asFlow()
            .mapToOneOrNull()
            .mapNotNull { it }

    fun insertPodcast(podcast: Podcast) {
        database.podcastQueries
            .insertPodcast(
                podcastId = podcast.podcastId,
                name = podcast.name,
                url = podcast.url,
                artistName = podcast.artistName,
                artistId = podcast.artistId,
                artistUrl = podcast.artistUrl,
                description = podcast.description,
                feedUrl = podcast.feedUrl,
                releaseDateTime = podcast.releaseDateTime,
                artwork = podcast.artwork,
                trackCount = podcast.trackCount,
                podcastWebsiteURL = podcast.podcastWebsiteURL,
                copyright = podcast.copyright,
                contentAdvisoryRating = podcast.contentAdvisoryRating,
                userRating = podcast.userRating,
                genreId = podcast.genreId
            )
    }

    fun updatePodcastMetadata(
        podcastId: Long,
        releaseDateTime: Instant,
        trackCount: Long)
    {
        database.podcastQueries
            .updatePodcastMetadata(releaseDateTime, trackCount, podcastId)
    }

    fun subscribeToPodcast(
        podcastId: Long,
        newEpisodeAction: NewEpisodesAction
    ) {
        database.podcastQueries
            .subscribe(newEpisodeAction = newEpisodeAction, podcastId = podcastId)
    }

    fun unsubscribeFromPodcast(podcastId: Long) {
        database.podcastQueries
            .unsubscribe(podcastId = podcastId)
    }

    fun deletePodcastById(id: Long) {
        database.podcastQueries
            .deletePodcastById(id)
    }

    fun deleteAllPodcasts() {
        database.podcastQueries.deleteAllPodcasts()
    }

}