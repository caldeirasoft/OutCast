package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.models.store.StorePodcastPage

class FetchStorePodcastDataUseCase constructor(
    val storeRepository: StoreRepository,
) {
    suspend fun execute(url: String, storeFront: String) : StorePodcastPage =
        storeRepository.getPodcastDataAsync(url, storeFront)
}