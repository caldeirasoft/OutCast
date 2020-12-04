package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.repository.DataStoreRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.DataState
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.*

class FetchStorePodcastDataUseCase constructor(
    val storeRepository: StoreRepository,
) {
    fun execute(url: String, storeFront: String) : Flow<StorePodcast> = flow {
        val storeData = storeRepository.getPodcastDataAsync(url, storeFront)
        emit(storeData)
    }
}