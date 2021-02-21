package com.caldeirasoft.outcast.domain.repository;

import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

/**
 * Created by Edmond on 15/02/2018.
 */

interface PodcastRepository {

    /**
     * Get the subscribed podcasts from DB
     */
    fun fetchSubscribedPodcasts(): Flow<List<PodcastSummary>>

    /**
     * Gets the podcast from database
     */
    fun getPodcast(podcastId: Long): Flow<Podcast>

    /**
     * Insert podcast into database
     */
    fun insertPodcast(podcast: Podcast)

    /**
     * Update podcast metadata
     */
    fun updatePodcastMetadata(
        podcastId: Long,
        releaseDateTime: Instant,
        trackCount: Long,
    )

    /**
     * Update podcast subscription
     */
    fun subscribeToPodcast(
        podcastId: Long,
        newEpisodeAction: NewEpisodesAction,
    )

    /**
     * Update podcast subscription
     */
    fun unsubscribeFromPodcast( podcastId: Long )

    /**
     * Delete podcast by id
     */
    fun deletePodcastById(id: Long)

    /**
     * Delete all podcasts
     */
    fun deleteAllPodcasts()
}