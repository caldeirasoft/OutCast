package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.stopwatch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FetchStoreTopChartsPodcastsIdsUseCase(
    private val storeRepository: StoreRepository
) {
    fun execute(storeGenre: Int?, storeFront: String): Flow<List<Long>> =
        flow {
            emit(stopwatch("FetchStoreTopChartsPodcastsIdsUseCase - getTopChartsPodcastsIdsAsync") {
                storeRepository.getTopChartsPodcastsIdsAsync(storeGenre, storeFront, 200)
            })
        }
}