package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.EpisodeRepository
import com.caldeirasoft.outcast.domain.models.EpisodeWithInfos
import kotlinx.coroutines.flow.Flow

class FetchEpisodeUseCase(val episodeRepository: EpisodeRepository)
    : FlowUseCase<Long, EpisodeWithInfos> {
    override fun execute(param: Long): Flow<EpisodeWithInfos> {
        return episodeRepository.getEpisode(param)
    }
}