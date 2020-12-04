package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.SectionWithCount
import com.caldeirasoft.outcast.domain.repository.EpisodeRepository

class FetchCountEpisodesBySectionUseCase constructor(
    val episodeRepository: EpisodeRepository)
    : FlowUseCase<FetchCountEpisodesBySectionUseCase.Params, SectionWithCount> {
    override fun execute(param: Params) =
        episodeRepository.fetchCountEpisodesBySection(param.podcastId)

    data class Params(val podcastId: Long)
}