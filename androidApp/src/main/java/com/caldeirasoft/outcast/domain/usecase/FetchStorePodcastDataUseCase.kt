package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcastPage
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.*

class FetchStorePodcastDataUseCase constructor(
    val storeRepository: StoreRepository,
) {
    fun execute(url: String, storeFront: String) : Flow<Resource> = networkCall {
        storeRepository.getPodcastDataAsync(url, storeFront)
    }
}