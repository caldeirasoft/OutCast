package com.caldeirasoft.outcast.data.repository

import android.content.Context
import androidx.paging.DataSource
import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.dao.QueueDao
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.EpisodeWithPodcast
import com.caldeirasoft.outcast.data.db.entities.PodcastWithCount
import com.caldeirasoft.outcast.domain.models.Category
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class EpisodesRepository @Inject constructor(
    val context: Context,
    val episodeDao: EpisodeDao,
    val queueDao: QueueDao,
) {

    private var refreshingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    // get episode with Guid
    fun getEpisodeWithGuid(feedUrl: String, guid: String): Flow<EpisodeWithPodcast?> =
        episodeDao.getEpisodeWithGuid(feedUrl, guid)

    // get inbox episodes
    fun getInboxEpisodesDataSource(): DataSource.Factory<Int, Episode> =
        episodeDao.getInboxEpisodesDataSource()

    // get inbox podcasts & count
    fun getInboxEpisodesPodcastCount(): Flow<List<PodcastWithCount>> =
        episodeDao.getInboxEpisodesPodcastCount()

    // get episodes from podcasts
    fun getEpisodesDataSourceWithUrl(feedUrl: String): DataSource.Factory<Int, Episode> =
        episodeDao.getEpisodesDataSourceWithUrl(feedUrl)

    // get saved episodes
    fun getSavedEpisodesDataSource(): DataSource.Factory<Int, Episode> =
        episodeDao.getSavedEpisodesDataSource()

    // get saved episodes podcasts & count
    fun getSavedEpisodesPodcastCount(): Flow<List<PodcastWithCount>> =
        episodeDao.getSavedEpisodesPodcastCount()

    // get episodes history
    fun getEpisodesHistoryDataSource(): DataSource.Factory<Int, Episode> =
        episodeDao.getEpisodesHistoryDataSource()

    // get episodes history podcasts & count
    fun getEpisodesHistoryPodcastCount(): Flow<List<PodcastWithCount>> =
        episodeDao.getEpisodesHistoryPodcastCount()

    // save episode to library
    suspend fun saveEpisodeToLibrary(episode: Episode) {
        episodeDao.saveEpisodeToLibrary(episode.feedUrl, episode.guid)
    }

    // remove episode from saved episodes
    suspend fun deleteFromLibrary(episode: Episode) {
        episodeDao.deleteFromLibrary(episode.feedUrl, episode.guid)
    }
}