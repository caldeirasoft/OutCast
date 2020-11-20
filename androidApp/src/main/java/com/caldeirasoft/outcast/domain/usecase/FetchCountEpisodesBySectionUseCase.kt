package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.SectionWithCount
import com.caldeirasoft.outcast.domain.repository.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchCountEpisodesBySectionUseCase @Inject constructor(
    val episodeRepository: EpisodeRepository)
    : FlowUseCase<FetchCountEpisodesBySectionUseCase.Params, SectionWithCount> {
    override fun execute(param: Params) =
        episodeRepository.fetchCountEpisodesBySection(param.podcastId)

    data class Params(val podcastId: Long)
}