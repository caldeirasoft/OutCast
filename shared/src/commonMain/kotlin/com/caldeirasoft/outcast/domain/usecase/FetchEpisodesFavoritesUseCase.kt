package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.EpisodeSummary
import com.caldeirasoft.outcast.domain.repository.EpisodeRepository
import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import kotlinx.coroutines.flow.Flow


class FetchEpisodesFavoritesUseCase(
    val episodeRepository: EpisodeRepository)
    : UseCase<FetchEpisodesFavoritesUseCase.Params, List<EpisodeSummary>> {
    override fun invoke(params: Params): Flow<List<EpisodeSummary>> =
        when {
            params.podcastId != null -> episodeRepository.fetchEpisodesFavoritesByPodcastId(params.podcastId)
            else -> episodeRepository.fetchEpisodesFavorites()
        }

    data class Params(val podcastId: Long?)
}