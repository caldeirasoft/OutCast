package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.LocalCacheRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.*
import kotlinx.coroutines.flow.*

class FetchStoreTopChartsUseCase(
    private val storeRepository: StoreRepository,
    private val localCacheRepository: LocalCacheRepository
) {
    suspend fun execute(url: String, storeFront: String): List<Long> =
        stopwatch("FetchStoreDirectoryUseCase - getTopChartsAsync") {
            storeRepository.getTopChartsIdsAsync(url, storeFront)
        }
}