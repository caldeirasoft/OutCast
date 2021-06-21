package com.caldeirasoft.outcast.data.repository

import androidx.paging.DataSource
import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.data.db.dao.SearchDao
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.db.entities.SearchEntity
import kotlinx.serialization.json.Json
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