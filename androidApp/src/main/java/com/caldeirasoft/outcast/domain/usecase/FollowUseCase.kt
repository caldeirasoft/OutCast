package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FollowUseCase @Inject constructor(
    private val podcastsRepository: PodcastsRepository,
) {
    fun execute(feedUrl: String): Flow<Boolean> = flow {
        podcastsRepository.followPodcast(feedUrl = feedUrl)
        emit(true)
    }

    fun execute(storePodcast: StorePodcast): Flow<Boolean> = flow {
        // fetch remote podcast data
        podcastsRepository.followPodcast(feedUrl = storePodcast.feedUrl, updatePodcast = true)
        emit(true)
    }
}