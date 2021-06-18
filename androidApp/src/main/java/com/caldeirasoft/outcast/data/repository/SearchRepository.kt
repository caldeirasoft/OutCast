package com.caldeirasoft.outcast.data.repository

import android.content.Context
import androidx.datastore.dataStore
import androidx.paging.DataSource
import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.api.ItunesAPI
import com.caldeirasoft.outcast.data.api.ItunesSearchAPI
import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.data.db.dao.SearchDao
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.db.entities.SearchEntity
import com.caldeirasoft.outcast.data.util.local.StoreDataSerializer
import com.caldeirasoft.outcast.domain.dto.LockupResult
import com.caldeirasoft.outcast.domain.dto.LookupResultItem
import com.caldeirasoft.outcast.domain.dto.StorePageDto
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import com.caldeirasoft.outcast.domain.models.store.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val podcastDao: PodcastDao,
    private val episodeDao: EpisodeDao,
    private val searchDao: SearchDao,
    private val json: Json,
) {
    /**
     * Get search podcast result
     */
    fun searchPodcasts(term: String): DataSource.Factory<Int, Podcast> =
        podcastDao.searchPodcasts(term)

    /**
     * Get search episodes result
     */
    fun searchEpisodes(term: String): DataSource.Factory<Int, Episode> =
        episodeDao.searchEpisodes(term)

    /**
     * Search history
     */
    suspend fun getSearches(): List<String> =
        searchDao.getSearches()

    /**
     * Search history
     */
    suspend fun searchHistory(term: String): List<String> =
        searchDao.searchHistory(term)

    /**
     * Add to search history
     */
    suspend fun addToSearchHistory(term: String) =
        searchDao.insert(SearchEntity(term))

}