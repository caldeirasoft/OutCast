package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.repository.DataStoreRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkBoundResource
import com.caldeirasoft.outcast.domain.util.networkCall
import com.caldeirasoft.outcast.domain.util.stopwatch
import kotlinx.coroutines.flow.*

class FetchStoreDataUseCase(
    val storeRepository: StoreRepository,
) {
    fun execute(url: String, storeFront: String): Flow<Resource<StoreData>> =
        networkCall {
            storeRepository.getStoreDataAsync(url, storeFront)
        }
}