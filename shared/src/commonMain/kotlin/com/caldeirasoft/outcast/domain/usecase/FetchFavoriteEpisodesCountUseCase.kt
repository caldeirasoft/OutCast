package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.EpisodesCountByPodcast
import com.caldeirasoft.outcast.domain.repository.EpisodeRepository

class FetchFavoriteEpisodesCountUseCase (
    val episodeRepository: EpisodeRepository)
    : FlowUseCaseWithoutParams<List<EpisodesCountByPodcast>> {
    override fun execute() =
        episodeRepository.fetchCountEpisodesFavoritesByPodcast()
}