package com.caldeirasoft.outcast.data.repository

import androidx.paging.PagingSource
import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.Podcast
import com.squareup.sqldelight.android.paging.QueryDataSourceFactory
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LibraryRepository @Inject constructor(
    val database: Database,
) {
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

    fun loadPodcast(feedUrl: String): Flow<Podcast?> =
        database.podcastQueries
            .getByUrl(feedUrl = feedUrl)
            .asFlow()
            .mapToOneOrNull()

    fun subscribeToPodcast(
        feedUrl: String,
    ) {
        database.podcastQueries
            .subscribe(feedUrl = feedUrl)
    }

    fun unsubscribeFromPodcast(feedUrl: String) {
        database.podcastQueries
            .unsubscribe(feedUrl = feedUrl)
    }

    fun loadEpisodesByFeedUrl(feedUrl: String): Flow<List<Episode>> =
        database.episodeQueries
            .getAllByUrl(feedUrl = feedUrl)
            .asFlow()
            .mapToList()

    fun getEpisodesByPodcastIdPagingSourceFactory(feedUrl: String): () -> PagingSource<Int, Episode> =
        QueryDataSourceFactory(
            queryProvider = { limit, offset ->
                database.episodeQueries.getAllPagedByUrl(
                    feedUrl = feedUrl,
                    limit = limit,
                    offset = offset)
            },
            countQuery = database.episodeQueries.countAllByUrl(feedUrl = feedUrl),
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

    fun loadEpisode(episode: Episode): Flow<Episode?> =
        database.episodeQueries
            .getByGuid(feedUrl = episode.feedUrl, guid = episode.guid)
            .asFlow()
            .mapToOneOrNull()

    fun markEpisodeAsPlayed(episode: Episode) {
        database.episodeQueries
            .markEpisodeAsPlayed(feedUrl = episode.feedUrl, guid = episode.guid)
    }

    fun updateEpisodeFavoriteStatus(episode: Episode, isFavorite: Boolean) {
        when (isFavorite) {
            true -> database.episodeQueries.addToFavorites(feedUrl = episode.feedUrl,
                guid = episode.guid)
            else -> database.episodeQueries.removeFromFavorites(feedUrl = episode.feedUrl,
                guid = episode.guid)
        }
    }

    fun updateEpisodePlaybackPosition(episode: Episode, playbackPosition: Int?) {
        database.episodeQueries.addToHistory(
            playbackPosition = playbackPosition,
            feedUrl = episode.feedUrl, guid = episode.guid)
    }


    /**
     * addRecentEpisodesIntoQueueLast
     */
    fun updateQueueEpisodeLimit(feedUrl: String, limit: Int) {
        database.queueQueries.updateQueueEpisodeLimit(
            feedUrl = feedUrl,
            offset = limit.toLong())
    }
}