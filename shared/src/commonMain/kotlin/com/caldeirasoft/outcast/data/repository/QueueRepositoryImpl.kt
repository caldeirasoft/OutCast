package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.repository.QueueRepository
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

class QueueRepositoryImpl(val database: Database) : QueueRepository {
    override fun fetchQueue(): Flow<List<EpisodeSummary>> =
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

    override fun addToQueue(episode: Episode, queueIndex: Long) {
        database.queueQueries
            addToQueue(episode = episode, queueIndex = queueIndex)
    }

    override fun addToQueueNext(episode: Episode) {
        database.queueQueries
            addToQueueNext(episode = episode)
    }

    override fun addToQueueLast(episode: Episode) {
        database.queueQueries
            addToQueueLast(episode = episode)
    }

    override fun removeFromQueue(id: Long) {
        database.queueQueries
            removeFromQueue(id = id)
    }


}
