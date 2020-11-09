package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.PodcastRepository
import com.caldeirasoft.outcast.domain.usecase.base.UseCaseWithoutInput
import com.caldeirasoft.outcast.domain.models.PodcastSummary
import kotlinx.coroutines.flow.Flow


class FetchPodcastsSubscribedUseCase(val podcastRepository: PodcastRepository)
    : UseCaseWithoutInput<List<PodcastSummary>> {
    override fun invoke(): Flow<List<PodcastSummary>> =
        podcastRepository.fetchSubscribedPodcasts()
}