package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.repository.InboxRepository
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.datetime.Instant

class InboxRepositoryImpl(val database: Database) : InboxRepository {
    override fun fetchEpisodes(): Flow<List<EpisodeSummary>> =
        database.inboxQueries
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
                )
            })
            .asFlow()
            .mapToList()

    override fun fetchEpisodesByGenre(genreId: Int): Flow<List<EpisodeSummary>> =
        database.inboxQueries
            .selectEpisodesByGenreId(genreId = genreId,
                mapper = { episodeId: Long,
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
                    -> EpisodeSummary(
                    episodeId, name, podcastId, releaseDateTime, description, contentAdvisoryRating, artwork, duration, podcastEpisodeSeason, podcastEpisodeNumber, isFavorite, isPlayed, playbackPosition, isInQueue, isInInbox, isInHistory, updatedAt
                )})
            .asFlow()
            .mapToList()


    override fun fetchGenreIds(): Flow<List<Int>> =
        database.inboxQueries
            .selectGenreId(mapper = { genreId: Int? -> genreId ?: 0 })
            .asFlow()
            .mapToList()

    override fun addToInbox(episode: Episode) {
        database.inboxQueries
            .addToInbox(episodeId = episode.episodeId)
    }

    override fun removeFromInbox(episodeId: Long) {
        database.inboxQueries
            .removeFromInbox(episodeId = episodeId)
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }
}
