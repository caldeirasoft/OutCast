package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode

class FetchStoreEpisodeDataUseCase constructor(
    val storeRepository: StoreRepository,
) {
    suspend fun execute(storeEpisode: StoreEpisode, storeFront: String) : StoreEpisode =
        storeRepository.getPodcastDataAsync(storeEpisode.url, storeFront)
            .let {
                it.episodes.first{ ep -> ep.id == storeEpisode.id }
            }
}