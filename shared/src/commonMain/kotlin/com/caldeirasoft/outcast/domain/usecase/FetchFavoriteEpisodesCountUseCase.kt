package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.EpisodesCountByPodcast
import com.caldeirasoft.outcast.domain.repository.EpisodeRepository
import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.usecase.base.UseCaseWithoutInput
import kotlinx.coroutines.flow.Flow


class FetchFavoriteEpisodesCountUseCase(
    val episodeRepository: EpisodeRepository)
    : UseCaseWithoutInput<List<EpisodesCountByPodcast>> {
    override fun invoke(): Flow<List<EpisodesCountByPodcast>> =
        episodeRepository.fetchCountEpisodesFavoritesByPodcast()
}