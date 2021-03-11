package com.caldeirasoft.outcast.data.repository

import androidx.paging.PagingSource
import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.db.EpisodeSummary
import com.caldeirasoft.outcast.db.EpisodeWithInfos
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.db.PodcastSummary
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.caldeirasoft.outcast.domain.models.PodcastPage
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.squareup.sqldelight.android.paging.QueryDataSourceFactory
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

    fun getEpisodesByPodcastIdPagingSourceFactory(podcastId: Long): () -> PagingSource<Int, EpisodeSummary> =
        QueryDataSourceFactory(
            queryProvider = { limit, offset ->
                database.episodeQueries.getAllPagedByPodcastId(podcastId = podcastId,
                    limit = limit,
                    offset = offset)
            },
            countQuery = database.episodeQueries.countAllByPodcastId(podcastId = podcastId),
        ).asPagingSourceFactory()

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
    fun updatePodcastAndEpisodes(remotePodcast: PodcastPage) {
        database.transaction {
            database.podcastQueries.insert(remotePodcast.podcast)
            remotePodcast.episodes.onEach {
                database.episodeQueries.insert(it)
            }
        }
    }
}