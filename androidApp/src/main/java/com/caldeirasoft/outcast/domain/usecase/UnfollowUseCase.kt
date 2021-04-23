package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import javax.inject.Inject

class UnfollowUseCase @Inject constructor(
    private val podcastRepository: PodcastsRepository,
) {
    suspend fun execute(feedUrl: String) {
        podcastRepository.unfollowPodcast(feedUrl = feedUrl)
    }
}