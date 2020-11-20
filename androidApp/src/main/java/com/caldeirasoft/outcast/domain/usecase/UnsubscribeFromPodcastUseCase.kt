package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.PodcastRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UnsubscribeFromPodcastUseCase @Inject constructor(val podcastRepository: PodcastRepository):
    FlowUseCase<UnsubscribeFromPodcastUseCase.Params, Unit> {

    override fun execute(params: Params): Flow<Unit> = flow {
        podcastRepository.unsubscribeFromPodcast(podcastId = params.podcastId)
    }

    data class Params(val podcastId: Long)
}