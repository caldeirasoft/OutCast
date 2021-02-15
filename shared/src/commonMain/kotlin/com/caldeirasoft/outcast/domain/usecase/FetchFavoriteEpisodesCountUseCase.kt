package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.EpisodeRepository
import com.caldeirasoft.outcast.domain.models.EpisodesCountByPodcast

class FetchFavoriteEpisodesCountUseCase (
    val episodeRepository: EpisodeRepository
)
    : FlowUseCaseWithoutParams<List<EpisodesCountByPodcast>> {
    override fun execute() =
        episodeRepository.fetchCountEpisodesFavoritesByPodcast()
}