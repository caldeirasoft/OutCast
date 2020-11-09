package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.PodcastRepository
import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UnsubscribeFromPodcastUseCase(val podcastRepository: PodcastRepository):
    UseCase<UnsubscribeFromPodcastUseCase.Params, Unit> {

    override fun invoke(params: Params): Flow<Unit> = flow {
        podcastRepository.unsubscribeFromPodcast(podcastId = params.podcastId)
    }

    data class Params(val podcastId: Long)
}