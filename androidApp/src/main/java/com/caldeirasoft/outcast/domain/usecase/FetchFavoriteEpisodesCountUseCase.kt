package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.EpisodesCountByPodcast
import com.caldeirasoft.outcast.domain.repository.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchFavoriteEpisodesCountUseCase @Inject constructor(
    val episodeRepository: EpisodeRepository)
    : FlowUseCaseWithoutParams<List<EpisodesCountByPodcast>> {
    override fun execute() =
        episodeRepository.fetchCountEpisodesFavoritesByPodcast()
}