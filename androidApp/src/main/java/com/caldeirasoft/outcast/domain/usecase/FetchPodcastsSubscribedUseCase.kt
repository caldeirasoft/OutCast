package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.PodcastRepository
import com.caldeirasoft.outcast.domain.models.PodcastSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchPodcastsSubscribedUseCase @Inject constructor(val podcastRepository: PodcastRepository)
    : UseCaseWithoutInput<List<PodcastSummary>> {
    override fun invoke(): Flow<List<PodcastSummary>> =
        podcastRepository.fetchSubscribedPodcasts()
}