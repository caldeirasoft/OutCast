package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.PodcastRepository
import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SubscribeToPodcastUseCase(val podcastRepository: PodcastRepository):
    UseCase<SubscribeToPodcastUseCase.Params, Unit> {

    override fun invoke(params: Params): Flow<Unit> = flow {
        podcastRepository.subscribeToPodcast(podcastId = params.podcastId, newEpisodeAction = params.newEpisodesAction)
    }

    data class Params(
        val podcastId: Long,
        val newEpisodesAction: NewEpisodesAction)
}