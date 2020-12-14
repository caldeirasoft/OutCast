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
     * getDirectoryDataAsync
     */
    suspend fun getDirectoryDataAsync(storeFront: String): StorePage

    /**
     * getStoreDataAsync
     */
    suspend fun getStoreDataAsync(url: String, storeFront: String): StorePage

    /**
     * getPodcastDataAsync
     */
    suspend fun getPodcastDataAsync(url: String, storeFront: String): StorePodcastPage

    /**
     * getTopChartsAsync
     */
    suspend fun getTopChartsAsync(url: String, storeFront: String): StoreTopCharts

    /**
     * getListStoreItemDataAsync
     */
    suspend fun getListStoreItemDataAsync(lookupIds: List<Long>, storeFront: String, storePage: StorePage?): List<StoreItem>

    /**
     * getGenresDataAsync
     */
    suspend fun getGenresDataAsync(storeFront: String): StoreGenreMapData

}