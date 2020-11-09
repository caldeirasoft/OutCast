package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.repository.EpisodeRepository
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.datetime.Instant

class EpisodeRepositoryImpl(val database: Database) : EpisodeRepository {
    override fun fetchEpisodesByPodcastId(podcastId: Long): Flow<List<EpisodeSummary>> =
        database.episodeQueries
            .selectAllByPodcastId(podcastId,
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


    override fun fetchEpisodesFavorites(): Flow<List<EpisodeSummary>> =
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

    override fun fetchEpisodesHistory(): Flow<List<EpisodeSummary>> =
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

    override fun fetchEpisodesFromQueueByPodcastId(podcastId: Long): Flow<List<EpisodeSummary>> =
        database.episodeQueries
            .selectEpisodesFromQueueByPodcastId(podcastId,
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

    override fun fetchEpisodesFromInboxByPodcastId(podcastId: Long): Flow<List<EpisodeSummary>> =
        database.episodeQueries
            .selectEpisodesFromInboxByPodcastId(podcastId,
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

    override fun fetchEpisodesFavoritesByPodcastId(podcastId: Long): Flow<List<EpisodeSummary>> =
        database.episodeQueries
            .selectFavoritesByPodcastId(podcastId,
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

    override fun fetchEpisodesHistoryByPodcastId(podcastId: Long): Flow<List<EpisodeSummary>> =
        database.episodeQueries
            .selectHistoryByPodcastId(podcastId,
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

    override fun fetchCountEpisodesFavoritesByPodcast(): Flow<List<EpisodesCountByPodcast>> =
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

    override fun fetchCountEpisodesPlayedByPodcast(): Flow<List<EpisodesCountByPodcast>> =
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

    override fun fetchCountEpisodesBySection(podcastId: Long): Flow<SectionWithCount> =
        database.episodeQueries
            .selectSectionWithCount(podcastId,
            mapper = { podcastId: Long,
                       queueCount: Long?,
                       inboxCount: Long?,
                       favoritesCount: Long?,
                       historyCount: Long?
                ->
                SectionWithCount(podcastId, queueCount, inboxCount, favoritesCount, historyCount)
            })
            .asFlow()
            .mapToOne()


    override fun getEpisode(episodeId: Long): Flow<EpisodeWithInfos> =
        database.episodeQueries
            .selectEpisodeById(episodeId = episodeId,
            mapper = { episodeId: Long,
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
                    episodeId,
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

    override fun insertEpisode(episode: Episode) {
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

    override fun insertEpisodes(list: List<Episode>) {
        database.episodeQueries.transaction {
            list.forEach { episode ->
                insertEpisode(episode = episode)
            }
        }
    }

    override fun markEpisodeAsPlayed(episodeId: Long) {
        database.episodeQueries
            .markEpisodeAsPlayed(episodeId = episodeId)
    }

    override fun markEpisodeAsUnplayed(episodeId: Long) {
        database.episodeQueries
            .markEpisodeAsUnplayed(episodeId = episodeId)
    }

    override fun updateEpisodeFavoriteStatus(episodeId: Long, isFavorite: Boolean) {
        when(isFavorite) {
            true -> database.episodeQueries.addToFavorites(episodeId)
            else -> database.episodeQueries.removeFromFavorites(episodeId)
        }
    }

    override fun updateEpisodePlaybackPosition(episodeId: Long, playbackPosition: Long?) {
        database.episodeQueries.addToHistory(playbackPosition = playbackPosition, episodeId = episodeId)
    }

    override fun deleteEpisodeById(episodeId: Long) {
        database.episodeQueries
                .deleteEpisodeById(episodeId)
    }

    override fun deleteAll() {
        database.episodeQueries
            .deleteAll()
    }
}