package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.EpisodeRepository
import com.caldeirasoft.outcast.domain.models.EpisodeSummary

class FetchEpisodesFavoritesUseCase(
    val episodeRepository: EpisodeRepository
)
    : FlowUseCase<FetchEpisodesFavoritesUseCase.Params, List<EpisodeSummary>> {
    override fun execute(param: Params) =
        when {
            param.podcastId != null -> episodeRepository.fetchEpisodesFavoritesByPodcastId(param.podcastId)
            else -> episodeRepository.fetchEpisodesFavorites()
        }

    data class Params(val podcastId: Long?)
}