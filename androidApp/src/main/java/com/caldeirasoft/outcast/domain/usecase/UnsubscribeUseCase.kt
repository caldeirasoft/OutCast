package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import javax.inject.Inject

class UnsubscribeUseCase @Inject constructor(
    val podcastRepository: PodcastsRepository,
) {
    suspend fun execute(feedUrl: String) {
        podcastRepository.unsubscribeFromPodcast(feedUrl = feedUrl)
    }
}