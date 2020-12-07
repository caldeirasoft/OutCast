package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcastPage
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import kotlinx.coroutines.flow.*

class FetchStorePodcastDataUseCase constructor(
    val storeRepository: StoreRepository,
) {
    fun execute(url: String, storeFront: String) : Flow<StorePodcastPage> = flow {
        val storeData = storeRepository.getPodcastDataAsync(url, storeFront)
        emit(storeData)
    }
}