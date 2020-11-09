package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.EpisodeWithInfos
import com.caldeirasoft.outcast.domain.repository.EpisodeRepository
import com.caldeirasoft.outcast.domain.repository.PodcastRepository
import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import kotlinx.coroutines.flow.Flow

class GetEpisodeUseCase(val episodeRepository: EpisodeRepository)
    : UseCase<GetEpisodeUseCase.Params, EpisodeWithInfos> {
    override fun invoke(params: Params): Flow<EpisodeWithInfos> {
        return episodeRepository.getEpisode(params.episodeId)
    }

    data class Params(val episodeId: Long)
}