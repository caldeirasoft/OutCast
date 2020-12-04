package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.models.StoreGroupingData
import com.caldeirasoft.outcast.domain.models.StoreTopCharts
import com.caldeirasoft.outcast.domain.repository.DataStoreRepository
import com.caldeirasoft.outcast.domain.repository.LocalCacheRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.*
import kotlinx.coroutines.flow.*

class FetchStoreTopChartsUseCase(
    private val storeRepository: StoreRepository,
    private val localCacheRepository: LocalCacheRepository
) {
    fun execute(storeFront: String): Flow<Resource<StoreTopCharts>> =
        networkCall {
            stopwatch("FetchStoreDirectoryUseCase - getTopChartsAsync") {
                storeRepository.getTopChartsAsync(storeFront)
            }
        }

    fun execute2(storeFront: String): Flow<Resource<StoreTopCharts>> =
        networkBoundResource(
            fetchFromLocal = { localCacheRepository.topCharts },
            fetchFromRemote = {
                stopwatch("FetchStoreDirectoryUseCase - getTopChartsAsync") {
                    storeRepository.getTopChartsAsync(storeFront)
                }
            },
            shouldFetchFromRemote = {
                it
                    ?.let { true }
                    ?: false
            },
            saveRemoteData = {
                stopwatch("FetchStoreDirectoryUseCase - saveTopCharts") {
                    localCacheRepository.saveTopCharts(it)
                }
            }
        )
}