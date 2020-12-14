package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.repository.DataStoreRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkBoundResource
import com.caldeirasoft.outcast.domain.util.networkCall
import com.caldeirasoft.outcast.domain.util.stopwatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*

class FetchStoreDataUseCase(
    val storeRepository: StoreRepository
) {
    fun executeAsync(url: String, storeFront: String): Flow<Resource<StorePage>> =
        networkCall {
            stopwatch("FetchStoreDataUseCase - getStoreDataAsync") {
                storeRepository.getStoreDataAsync(url, storeFront)
            }
        }
}