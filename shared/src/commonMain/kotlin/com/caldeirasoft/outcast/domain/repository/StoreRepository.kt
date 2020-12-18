package com.caldeirasoft.outcast.domain.repository

import com.caldeirasoft.outcast.domain.dto.LockupResult
import com.caldeirasoft.outcast.domain.dto.ResultIdsResult
import com.caldeirasoft.outcast.domain.dto.StorePageDto
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.models.store.*

interface StoreRepository {

    /**
     * getGroupingDataAsync
     */
    suspend fun getGroupingDataAsync(genre: Int?, storeFront: String): StoreGroupingData

    /**
     * getStoreDataAsync
     */
    suspend fun getStoreDataAsync(url: String, storeFront: String): StorePage

    /**
     * getPodcastDataAsync
     */
    suspend fun getPodcastDataAsync(url: String, storeFront: String): StorePodcastPage

    /**
     * getTopChartsIdsAsync
     */
    suspend fun getTopChartsPodcastsIdsAsync(genre: Int?, storeFront: String, limit: Int): List<Long>

    /**
     * getTopChartsIdsAsync
     */
    suspend fun getTopChartsEpisodesIdsAsync(genre: Int?, storeFront: String, limit: Int): List<Long>

    /**
     * getListStoreItemDataAsync
     */
    suspend fun getListStoreItemDataAsync(lookupIds: List<Long>, storeFront: String, storePage: StorePage?): List<StoreItem>

    /**
     * getGenresDataAsync
     */
    suspend fun getGenresDataAsync(storeFront: String): StoreGenreData

}