package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.PodcastsRepository

class UnsubscribeUseCase constructor(
    val podcastRepository: PodcastsRepository,
) {
    suspend fun execute(feedUrl: String) {
        podcastRepository.unsubscribeFromPodcast(feedUrl = feedUrl)
    }
}