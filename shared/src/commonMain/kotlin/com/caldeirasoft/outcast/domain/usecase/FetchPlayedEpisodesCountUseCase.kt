package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.EpisodeRepository
import com.caldeirasoft.outcast.domain.models.EpisodesCountByPodcast
import kotlinx.coroutines.flow.Flow


class FetchPlayedEpisodesCountUseCase(
    val episodeRepository: EpisodeRepository)
    : FlowUseCaseWithoutParams<List<EpisodesCountByPodcast>> {
    override fun execute(): Flow<List<EpisodesCountByPodcast>> =
        episodeRepository.fetchCountEpisodesPlayedByPodcast()
}