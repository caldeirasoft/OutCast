package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.db.Podcast

class LoadPodcastEpisodesPagingDataUseCase(val storeRepository: StoreRepository) {
    fun execute(podcast: Podcast, storeFront: String) =
        storeRepository.loadPodcastEpisodesPagingData(podcast, storeFront)
}