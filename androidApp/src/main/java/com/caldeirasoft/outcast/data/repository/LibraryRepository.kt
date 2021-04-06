package com.caldeirasoft.outcast.data.repository

import androidx.paging.PagingSource
import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.squareup.sqldelight.android.paging.QueryDataSourceFactory
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow

class LibraryRepository(
    val database: Database
)
{
    fun loadAllPodcasts(): Flow<List<Podcast>> =
        database.podcastQueries
            .getAll()
            .asFlow()
            .mapToList()

    fun loadFollowedPodcasts(): Flow<List<Podcast>> =
        database.podcastQueries
            .getSubscribed()
            .asFlow()
            .mapToList()

    fun loadPodcast(podcastId: Long): Flow<Podcast?> =
        database.podcastQueries
            .getById(podcastId = podcastId)
            .asFlow()
            .mapToOneOrNull()

    fun subscribeToPodcast(
        podcastId: Long,
        newEpisodeAction: NewEpisodesAction,
    ) {
        database.podcastQueries
            .subscribe(newEpisodeAction = newEpisodeAction, podcastId = podcastId)
    }

    fun unsubscribeFromPodcast(podcastId: Long) {
        database.podcastQueries
            .unsubscribe(podcastId = podcastId)
    }

    fun loadEpisodesByPodcastId(podcastId: Long): Flow<List<Episode>> =
        database.episodeQueries
            .getAllByPodcastId(podcastId = podcastId)
            .asFlow()
            .mapToList()

    fun getEpisodesByPodcastIdPagingSourceFactory(podcastId: Long): () -> PagingSource<Int, Episode> =
        QueryDataSourceFactory(
            queryProvider = { limit, offset ->
                database.episodeQueries.getAllPagedByPodcastId(
                    podcastId = podcastId,
                    limit = limit,
                    offset = offset)
            },
            countQuery = database.episodeQueries.countAllByPodcastId(podcastId = podcastId),
        ).asPagingSourceFactory()

    fun loadEpisodesFavorites(): Flow<List<Episode>> =
        database.episodeQueries
            .getFavorites()
            .asFlow()
            .mapToList()

    fun loadEpisodesHistory(): Flow<List<Episode>> =
        database.episodeQueries
            .getHistory()
            .asFlow()
            .mapToList()

    fun loadEpisode(episodeId: Long): Flow<Episode?> =
        database.episodeQueries
            .getById(episodeId = episodeId)
            .asFlow()
            .mapToOneOrNull()

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

    fun updateEpisodePlaybackPosition(episodeId: Long, playbackPosition: Int?) {
        database.episodeQueries.addToHistory(playbackPosition = playbackPosition,
            episodeId = episodeId)
    }

    /**
     * doesPodcastNeedUpdate
     */
    fun doesPodcastNeedUpdate(podcastId: Long, podcastLookup: StorePodcast): Boolean {
        val podcastDb = database.podcastQueries.getById(podcastId).executeAsOneOrNull()
        return podcastDb?.let {
            if (podcastLookup.releaseDateTime == podcastDb.releaseDateTime) {
                database.podcastQueries.updateLastAccess(podcastDb.podcastId)
                false
            } else {
                database.podcastQueries.updateMetadata(podcastLookup.releaseDateTime,
                    podcastLookup.trackCount.toLong(),
                    podcastId)
                true
            }
        } ?: true
    }

    /**
     * updatePodcastAndEpisodes
     */
    fun updatePodcastAndEpisodes(remotePodcast: StorePodcast) {
        database.transaction {
            database.podcastQueries.insert(remotePodcast.podcast)
            remotePodcast.episodes.onEach {
                database.episodeQueries.insert(it)
            }
        }
    }
}