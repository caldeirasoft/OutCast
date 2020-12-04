package com.caldeirasoft.outcast.domain.repository

import com.caldeirasoft.outcast.domain.dto.LockupResult
import com.caldeirasoft.outcast.domain.dto.StoreFrontDto
import com.caldeirasoft.outcast.domain.dto.StorePageDto
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.*

interface StoreRepository {

    /**
     * getDirectoryDataAsync
     */
    suspend fun getDirectoryDataAsync(storeFront: String): StoreGroupingData

    /**
     * getStoreDataAsync
     */
    suspend fun getStoreDataAsync(url: String, storeFront: String): StoreData

    /**
     * getPodcastDataAsync
     */
    suspend fun getPodcastDataAsync(url: String, storeFront: String): StorePodcast

    /**
     * getTopChartsAsync
     */
    suspend fun getTopChartsAsync(storeFront: String): StoreTopCharts

    /**
     * getListStoreItemDataAsync
     */
    suspend fun getListStoreItemDataAsync(lookupIds: List<Long>, storePage: StorePage): List<StoreItem>

    /**
     * getLookupDataAsync
     */
    suspend fun getLookupDataAsync(lookupIds: List<Long>, storeFront: String): LockupResult

    /**
     * getStoreDataApi
     */
    suspend fun getStoreDataApi(url: String, storeFront: String): StorePageDto
}