package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.models.Episode
import com.caldeirasoft.outcast.domain.models.EpisodeSummary
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

class QueueRepository(val database: Database) {
    fun fetchQueue(): Flow<List<EpisodeSummary>> =
        database.queueQueries
            .selectAll(mapper = { episodeId: Long,
                                  name: String,
                                  podcastId: Long,
                                  releaseDateTime: String,
                                  description: String?,
                                  contentAdvisoryRating: String?,
                                  artwork: Artwork?,
                                  duration: Long,
                                  podcastEpisodeSeason: Long?,
                                  podcastEpisodeNumber: Long?,
                                  isFavorite: Boolean,
                                  isPlayed: Boolean,
                                  playbackPosition: Long?,
                                  isInQueue: Long,
                                  isInInbox: Long,
                                  isInHistory: Long,
                                  updatedAt: Instant
                ->
                EpisodeSummary(
                    episodeId,
                    name,
                    podcastId,
                    releaseDateTime,
                    description,
                    contentAdvisoryRating,
                    artwork,
                    duration,
                    podcastEpisodeSeason,
                    podcastEpisodeNumber,
                    isFavorite,
                    isPlayed,
                    playbackPosition,
                    isInQueue,
                    isInInbox,
                    isInHistory,
                    updatedAt
                )})
            .asFlow()
            .mapToList()

    fun addToQueue(episode: Episode, queueIndex: Long) {
        database.queueQueries
            addToQueue(episode = episode, queueIndex = queueIndex)
    }

    fun addToQueueNext(episode: Episode) {
        database.queueQueries
            addToQueueNext(episode = episode)
    }

    fun addToQueueLast(episode: Episode) {
        database.queueQueries
            addToQueueLast(episode = episode)
    }

    fun removeFromQueue(id: Long) {
        database.queueQueries
            removeFromQueue(id = id)
    }


}
