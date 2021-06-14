package com.caldeirasoft.outcast.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.paging.DataSource
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.common.PodcastPreferenceKeys
import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.dao.InboxDao
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.data.db.dao.QueueDao
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.db.entities.PodcastItunesMetadata
import com.caldeirasoft.outcast.data.util.PodcastsFetcher
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.podcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

class EpisodesRepository @Inject constructor(
    val context: Context,
    val episodeDao: EpisodeDao,
    val queueDao: QueueDao,
) {

    private var refreshingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    // get inbox episodes
    fun getInboxEpisodesDataSource(): DataSource.Factory<Int, Episode> =
        episodeDao.getInboxEpisodesDataSource()

    // get inbox categories
    fun getInboxEpisodesCategories(): Flow<List<Category?>> =
        episodeDao.getInboxEpisodesCategories()
            .map { categories -> categories.map { index -> index?.let { Category.values()[index] } } }


    // get episodes from podcasts
    fun getEpisodesDataSourceWithUrl(feedUrl: String): DataSource.Factory<Int, Episode> =
        episodeDao.getEpisodesDataSourceWithUrl(feedUrl)
}