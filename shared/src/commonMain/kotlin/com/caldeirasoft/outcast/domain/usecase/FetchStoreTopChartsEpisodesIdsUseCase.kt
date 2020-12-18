package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.stopwatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchStoreTopChartsEpisodesIdsUseCase(
    private val storeRepository: StoreRepository
) {
    fun execute(storeGenre: Int?, storeFront: String): Flow<List<Long>> =
        flow {
            emit(stopwatch("FetchStoreTopChartsEpisodesIdsUseCase - getTopChartsEpisodesIdsAsync") {
                storeRepository.getTopChartsEpisodesIdsAsync(storeGenre, storeFront, 200)
            })
        }
}