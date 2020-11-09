package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.Podcast
import com.caldeirasoft.outcast.domain.repository.PodcastRepository
import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import kotlinx.coroutines.flow.Flow

class GetPodcastUseCase(val podcastRepository: PodcastRepository)
    : UseCase<GetPodcastUseCase.Params, Podcast> {
    override fun invoke(params: Params): Flow<Podcast> {
        return podcastRepository.getPodcast(params.podcastId)
    }

    data class Params(val podcastId: Long)
}