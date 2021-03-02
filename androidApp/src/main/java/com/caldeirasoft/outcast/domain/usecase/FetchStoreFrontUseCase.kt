package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class FetchStoreFrontUseCase(
    val dataStoreRepository: DataStoreRepository
) {

    fun getCountry(): Flow<String> =
            dataStoreRepository.storeCountry

    fun getStoreFront(): Flow<String> =
        getCountry()
            .map {
                Timber.d("DBG - getStoreFront")
                dataStoreRepository.getCurrentStoreFront(it)
            }
}