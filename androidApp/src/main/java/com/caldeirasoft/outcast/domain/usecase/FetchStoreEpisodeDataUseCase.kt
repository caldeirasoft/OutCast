package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.db.Episode

class FetchStoreEpisodeDataUseCase constructor(
    val storeRepository: StoreRepository,
) {
    suspend fun execute(storeEpisode: Episode, storeFront: String) : Episode =
        storeRepository.getPodcastDataAsync(storeEpisode.url, storeFront)
            .let {
                it.episodes.first{ ep -> ep.episodeId == storeEpisode.episodeId }
            }
}