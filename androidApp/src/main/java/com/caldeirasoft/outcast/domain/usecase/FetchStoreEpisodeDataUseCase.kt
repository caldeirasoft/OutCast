package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkCall
import kotlinx.coroutines.flow.Flow

class FetchStoreEpisodeDataUseCase constructor(
    val storeRepository: StoreRepository,
) {
    fun execute(storeEpisode: StoreEpisode, storeFront: String) : Flow<Resource> =
        networkCall {
            storeRepository.getPodcastDataAsync(storeEpisode.url, storeFront)
                .let {
                    it.episodes.first{ ep -> ep.id == storeEpisode.id }
                }
        }
}