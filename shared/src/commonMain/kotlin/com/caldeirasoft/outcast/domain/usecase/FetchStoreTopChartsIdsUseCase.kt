package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.store.StoreTopCharts
import com.caldeirasoft.outcast.domain.repository.LocalCacheRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.*
import kotlinx.coroutines.flow.*

class FetchStoreTopChartsIdsUseCase(
    private val storeRepository: StoreRepository,
    private val localCacheRepository: LocalCacheRepository
) {
    fun execute(url: String, storeFront: String): Flow<List<Long>> =
        flow {
            emit(stopwatch("FetchStoreDirectoryUseCase - getTopChartsIdsAsync") {
                storeRepository.getTopChartsIdsAsync(url, storeFront)
            })
        }
}