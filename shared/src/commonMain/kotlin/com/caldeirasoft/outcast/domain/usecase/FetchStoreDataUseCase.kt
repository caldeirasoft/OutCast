package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.repository.DataStoreRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import kotlinx.coroutines.flow.*

class FetchStoreDataUseCase(
    val storeRepository: StoreRepository,
) {
    fun execute(url: String, storeFront: String): Flow<StoreData> = flow {
        val storeData = storeRepository.getStoreDataAsync(url, storeFront)
        emit(storeData)
    }
}