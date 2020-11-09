package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.EpisodeRepository
import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.models.EpisodeSummary
import kotlinx.coroutines.flow.Flow


class FetchEpisodesHistoryUseCase(
    val episodeRepository: EpisodeRepository)
    : UseCase<FetchEpisodesHistoryUseCase.Params, List<EpisodeSummary>> {
    override fun invoke(params: Params): Flow<List<EpisodeSummary>> =
        when {
            params.podcastId != null -> episodeRepository.fetchEpisodesHistoryByPodcastId(params.podcastId)
            else -> episodeRepository.fetchEpisodesHistory()
        }

    data class Params(val podcastId: Long?)
}