package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.EpisodeWithInfos
import com.caldeirasoft.outcast.domain.repository.EpisodeRepository
import kotlinx.coroutines.flow.Flow

class FetchEpisodeUseCase(val episodeRepository: EpisodeRepository)
    : FlowUseCase<Long, EpisodeWithInfos> {
    override fun execute(param: Long): Flow<EpisodeWithInfos> {
        return episodeRepository.getEpisode(param)
    }
}