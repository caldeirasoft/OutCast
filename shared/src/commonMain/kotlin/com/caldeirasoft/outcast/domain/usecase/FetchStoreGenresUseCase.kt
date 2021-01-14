package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import com.caldeirasoft.outcast.domain.util.stopwatch
import kotlinx.coroutines.flow.Flow

class FetchStoreGenresUseCase(
    val storeRepository: StoreRepository,
) {
    suspend fun execute(storeFront: String): Flow<Resource> =
        networkCall {
            stopwatch("FetchStoreGenresUseCase - getGenresDataAsync") {
                storeRepository.getGenresDataAsync(storeFront)
            }
        }
}