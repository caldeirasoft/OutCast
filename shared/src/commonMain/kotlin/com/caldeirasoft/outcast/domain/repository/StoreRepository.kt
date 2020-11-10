package com.caldeirasoft.outcast.domain.repository

import com.caldeirasoft.outcast.domain.dto.LockupResult
import com.caldeirasoft.outcast.domain.dto.LookupResultItem
import com.caldeirasoft.outcast.domain.dto.StorePageDto
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.util.NetworkResponse
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkBoundResource
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy

interface StoreRepository {

    /**
     * getDirectoryDataAsync
     */
    suspend fun getDirectoryDataAsync(storeFront: String): NetworkResponse<StoreDataGrouping>

    /**
     * getStoreDataAsync
     */
    suspend fun getStoreDataAsync(url: String, storeFront: String): NetworkResponse<StoreData>

    /**
     * getPodcastDataAsync
     */
    suspend fun getPodcastDataAsync(url: String, storeFront: String): NetworkResponse<StoreDataPodcast>

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