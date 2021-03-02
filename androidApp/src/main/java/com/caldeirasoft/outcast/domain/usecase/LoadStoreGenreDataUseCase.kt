package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.models.store.StoreGenreData
import com.caldeirasoft.outcast.domain.util.stopwatch

class LoadStoreGenreDataUseCase(
    val storeRepository: StoreRepository,
) {
    suspend fun execute(storeFront: String): StoreGenreData =
        stopwatch("FetchStoreGenresUseCase - getGenresDataAsync") {
            storeRepository.loadStoreGenreData(storeFront)
        }
}