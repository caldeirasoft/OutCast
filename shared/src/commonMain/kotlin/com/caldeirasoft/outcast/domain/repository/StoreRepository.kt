package com.caldeirasoft.outcast.domain.repository

import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.store.StoreGenreData
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingData
import com.caldeirasoft.outcast.domain.models.store.StorePodcastPage

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
    suspend fun getTopChartsIdsAsync(genre: Int?, storeFront: String, storeItemType: StoreItemType, limit: Int): List<Long>

    /**
     * getListStoreItemDataAsync
     */
    suspend fun getListStoreItemDataAsync(lookupIds: List<Long>, storeFront: String, storePage: StorePage?): List<StoreItem>

    /**
     * getGenresDataAsync
     */
    suspend fun getGenresDataAsync(storeFront: String): StoreGenreData

}