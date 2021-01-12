package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import com.caldeirasoft.outcast.domain.util.stopwatch
import kotlinx.coroutines.flow.Flow

class FetchStoreDataUseCase(
    val storeRepository: StoreRepository
) {
    fun executeAsync(url: String, storeFront: String): Flow<Resource> =
        networkCall {
            stopwatch("FetchStoreDataUseCase - getStoreDataAsync") {
                storeRepository.getStoreDataAsync(url, storeFront)
            }
        }
}