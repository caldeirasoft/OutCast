package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class FetchStoreFrontUseCase @Inject constructor(
    val dataStoreRepository: DataStoreRepository,
) {

    fun getCountry(): Flow<String> =
        dataStoreRepository.storeCountry

    fun getStoreFront(): Flow<String> =
        dataStoreRepository.storeFront
}