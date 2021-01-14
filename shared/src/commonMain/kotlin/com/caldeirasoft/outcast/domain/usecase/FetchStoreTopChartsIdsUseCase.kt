package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.stopwatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchStoreTopChartsIdsUseCase(
    private val storeRepository: StoreRepository
) {
    fun execute(storeGenre: Int?, storeItemType: StoreItemType, storeFront: String): Flow<List<Long>> =
        flow {
            emit(stopwatch("FetchStoreTopChartsIdsUseCase - getTopChartsIdsAsync") {
                storeRepository.getTopChartsIdsAsync(storeGenre, storeFront, storeItemType, 200)
            })
        }
}