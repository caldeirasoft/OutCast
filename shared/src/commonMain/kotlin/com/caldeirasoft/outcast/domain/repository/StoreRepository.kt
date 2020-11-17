package com.caldeirasoft.outcast.domain.repository

import com.caldeirasoft.outcast.domain.dto.LockupResult
import com.caldeirasoft.outcast.domain.dto.StorePageDto
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StoreDataWithLookup
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.util.NetworkResponse

interface StoreRepository {

    /**
     * getDirectoryDataAsync
     */
    suspend fun getDirectoryDataAsync(storeFront: String): NetworkResponse<StoreGroupingData>

    /**
     * getStoreDataAsync
     */
    suspend fun getStoreDataAsync(url: String, storeFront: String): NetworkResponse<StoreData>

    /**
     * getPodcastDataAsync
     */
    suspend fun getPodcastDataAsync(url: String, storeFront: String): NetworkResponse<StorePodcast>

    /**
     * getListStoreItemDataAsync
     */
    suspend fun getListStoreItemDataAsync(lookupIds: List<Long>, storeFront: String, storeDataWithLookup: StoreDataWithLookup): NetworkResponse<List<StoreItem>>

    /**
     * getLookupDataAsync
     */
    suspend fun getLookupDataAsync(lookupIds: List<Long>, storeFront: String): NetworkResponse<LockupResult>

    /**
     * getStoreDataApi
     */
    suspend fun getStoreDataApi(url: String, storeFront: String): NetworkResponse<StorePageDto>
}