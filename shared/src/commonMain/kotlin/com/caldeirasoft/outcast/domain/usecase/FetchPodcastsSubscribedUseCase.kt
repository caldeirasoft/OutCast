package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.PodcastRepository
import com.caldeirasoft.outcast.domain.models.PodcastSummary
import kotlinx.coroutines.flow.Flow

class FetchPodcastsSubscribedUseCase(val podcastRepository: PodcastRepository)
    : FlowUseCaseWithoutParams<List<PodcastSummary>> {
    override fun execute(): Flow<List<PodcastSummary>> =
        podcastRepository.fetchSubscribedPodcasts()
}