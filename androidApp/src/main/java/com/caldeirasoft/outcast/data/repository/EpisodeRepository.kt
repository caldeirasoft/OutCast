package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.domain.models.*
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.datetime.Instant

class EpisodeRepository (val database: Database) {
    fun fetchEpisodesByPodcastId(id: Long): Flow<List<EpisodeSummary>> =
        database.episodeQueries
            .selectAllByPodcastId(id,
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


    fun fetchEpisodesFavorites(): Flow<List<EpisodeSummary>> =
        database.episodeQueries
            .selectAllFavorites(mapper = { episodeId: Long,
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

    fun fetchEpisodesHistory(): Flow<List<EpisodeSummary>> =
        database.episodeQueries
            .selectAllHistory(mapper = { episodeId: Long,
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

    fun fetchEpisodesFromQueueByPodcastId(id: Long): Flow<List<EpisodeSummary>> =
        database.episodeQueries
            .selectEpisodesFromQueueByPodcastId(id,
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

    fun fetchEpisodesFromInboxByPodcastId(id: Long): Flow<List<EpisodeSummary>> =
        database.episodeQueries
            .selectEpisodesFromInboxByPodcastId(id,
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

    fun fetchEpisodesFavoritesByPodcastId(id: Long): Flow<List<EpisodeSummary>> =
        database.episodeQueries
            .selectFavoritesByPodcastId(id,
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

    fun fetchEpisodesHistoryByPodcastId(id: Long): Flow<List<EpisodeSummary>> =
        database.episodeQueries
            .selectHistoryByPodcastId(id,
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

    fun fetchCountEpisodesFavoritesByPodcast(): Flow<List<EpisodesCountByPodcast>> =
        database.episodeQueries
            .selectFavoritesEpisodesCount(
                mapper = { podcastId: Long,
                           podcastName: String,
                           artwork: Artwork?,
                           episodeCount: Long
                    -> EpisodesCountByPodcast(podcastId, podcastName, artwork, episodeCount)
                })
            .asFlow()
            .mapToList()

    fun fetchCountEpisodesPlayedByPodcast(): Flow<List<EpisodesCountByPodcast>> =
        database.episodeQueries
            .selectPlayedEpisodesCount(
                mapper = { podcastId: Long,
                           podcastName: String,
                           artwork: Artwork?,
                           episodeCount: Long
                    ->
                    EpisodesCountByPodcast(podcastId, podcastName, artwork, episodeCount)
                })
            .asFlow()
            .mapToList()

    fun fetchCountEpisodesBySection(podcastId: Long): Flow<SectionWithCount> =
        database.episodeQueries
            .selectSectionWithCount(podcastId,
            mapper = { _podcastId: Long,
                       queueCount: Long?,
                       inboxCount: Long?,
                       favoritesCount: Long?,
                       historyCount: Long?
                ->
                SectionWithCount(_podcastId, queueCount, inboxCount, favoritesCount, historyCount)
            })
            .asFlow()
            .mapToOne()


    fun getEpisode(episodeId: Long): Flow<EpisodeWithInfos> =
        database.episodeQueries
            .selectEpisodeById(episodeId = episodeId,
            mapper = { _episodeId: Long,
                       name: String,
                       podcastId: Long,
                       podcastName: String,
                       artistName: String,
                       artistId: Long?,
                       releaseDateTime: Instant,
                       genre: List<Int>,
                       feedUrl: String,
                       description: String?,
                       contentAdvisoryRating: String?,
                       artwork: Artwork?,
                       mediaUrl: String,
                       mediaType: String,
                       duration: Long,
                       podcastEpisodeSeason: Long?,
                       podcastEpisodeNumber: Long?,
                       podcastEpisodeWebsiteUrl: String?,
                       isFavorite: Boolean,
                       isPlayed: Boolean,
                       playbackPosition: Long?,
                       isInQueue: Long,
                       isInInbox: Long,
                       isInHistory: Long
                ->
                EpisodeWithInfos(
                    _episodeId,
                    name,
                    podcastId,
                    podcastName,
                    artistName,
                    artistId,
                    releaseDateTime,
                    genre,
                    feedUrl,
                    description,
                    contentAdvisoryRating,
                    artwork,
                    mediaUrl,
                    mediaType,
                    duration,
                    podcastEpisodeSeason,
                    podcastEpisodeNumber,
                    podcastEpisodeWebsiteUrl,
                    isFavorite,
                    isPlayed,
                    playbackPosition,
                    isInQueue,
                    isInInbox,
                    isInHistory
                )
            })
            .asFlow()
            .mapToOneOrNull()
            .mapNotNull { it }

    fun insertEpisode(episode: Episode) {
        database.episodeQueries
            .insertEpisode(
                episodeId = episode.episodeId,
                name = episode.name,
                podcastId = episode.podcastId,
                podcastName = episode.podcastName,
                artistName = episode.artistName,
                artistId = episode.artistId,
                releaseDateTime = episode.releaseDateTime,
                genre = episode.genre,
                feedUrl = episode.feedUrl,
                description = episode.description,
                contentAdvisoryRating = episode.contentAdvisoryRating,
                artwork = episode.artwork,
                mediaUrl = episode.mediaUrl,
                mediaType = episode.mediaType,
                duration = episode.duration,
                podcastEpisodeSeason = episode.podcastEpisodeSeason,
                podcastEpisodeNumber = episode.podcastEpisodeNumber,
                podcastEpisodeWebsiteUrl = episode.podcastEpisodeWebsiteUrl
            )
    }

    fun insertEpisodes(list: List<Episode>) {
        database.episodeQueries.transaction {
            list.forEach { episode ->
                insertEpisode(episode = episode)
            }
        }
    }

    fun markEpisodeAsPlayed(episodeId: Long) {
        database.episodeQueries
            .markEpisodeAsPlayed(episodeId = episodeId)
    }

    fun markEpisodeAsUnplayed(episodeId: Long) {
        database.episodeQueries
            .markEpisodeAsUnplayed(episodeId = episodeId)
    }

    fun updateEpisodeFavoriteStatus(episodeId: Long, isFavorite: Boolean) {
        when(isFavorite) {
            true -> database.episodeQueries.addToFavorites(episodeId)
            else -> database.episodeQueries.removeFromFavorites(episodeId)
        }
    }

    fun updateEpisodePlaybackPosition(episodeId: Long, playbackPosition: Long?) {
        database.episodeQueries.addToHistory(playbackPosition = playbackPosition, episodeId = episodeId)
    }

    fun deleteEpisodeById(episodeId: Long) {
        database.episodeQueries
                .deleteEpisodeById(episodeId)
    }

    fun deleteAll() {
        database.episodeQueries
            .deleteAll()
    }
}