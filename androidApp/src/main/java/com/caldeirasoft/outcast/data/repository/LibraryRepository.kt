package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.db.EpisodeSummary
import com.caldeirasoft.outcast.db.EpisodeWithInfos
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.db.PodcastSummary
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class LibraryRepository(
    val database: Database
)
{
    fun loadAllPodcasts(): Flow<List<PodcastSummary>> =
        database.podcastQueries
            .getAll()
            .asFlow()
            .mapToList()


    fun loadPodcast(podcastId: Long): Flow<Podcast> =
        database.podcastQueries
            .getById(podcastId = podcastId)
            .asFlow()
            .mapToOneOrNull()
            .mapNotNull { it }

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

    fun loadEpisodesByPodcastId(podcastId: Long): Flow<List<EpisodeSummary>> =
        database.episodeQueries
            .getAllByPodcastId(podcastId = podcastId)
            .asFlow()
            .mapToList()

    fun loadEpisodesFavorites(): Flow<List<EpisodeSummary>> =
        database.episodeQueries
            .getFavorites()
            .asFlow()
            .mapToList()

    fun loadEpisodesHistory(): Flow<List<EpisodeSummary>> =
        database.episodeQueries
            .getHistory()
            .asFlow()
            .mapToList()

    fun loadEpisode(episodeId: Long): Flow<EpisodeWithInfos> =
        database.episodeQueries
            .getById(episodeId = episodeId)
            .asFlow()
            .mapToOneOrNull()
            .mapNotNull { it }

    fun markEpisodeAsPlayed(episodeId: Long) {
        database.episodeQueries
            .markEpisodeAsPlayed(episodeId = episodeId)
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

}