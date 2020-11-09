package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.SectionWithCount
import com.caldeirasoft.outcast.domain.repository.EpisodeRepository
import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import kotlinx.coroutines.flow.Flow


class FetchCountEpisodesBySectionUseCase(
    val episodeRepository: EpisodeRepository)
    : UseCase<FetchCountEpisodesBySectionUseCase.Params, SectionWithCount> {
    override fun invoke(params:Params): Flow<SectionWithCount> =
        episodeRepository.fetchCountEpisodesBySection(params.podcastId)

    data class Params(val podcastId: Long)
}