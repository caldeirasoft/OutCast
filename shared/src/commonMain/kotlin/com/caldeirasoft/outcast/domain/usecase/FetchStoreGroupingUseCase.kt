package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.LocalCacheRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import com.caldeirasoft.outcast.domain.util.stopwatch
import kotlinx.coroutines.flow.Flow

class FetchStoreGroupingUseCase(
    val storeRepository: StoreRepository,
    val localCacheRepository: LocalCacheRepository
)
{
    fun executeAsync(genreId: Int?, storeFront: String): Flow<Resource> =
        networkCall {
            stopwatch("FetchStoreGroupingUseCase - fetchGroupingData") {
                storeRepository.getGroupingDataAsync(genreId, storeFront)
            }
        }
}