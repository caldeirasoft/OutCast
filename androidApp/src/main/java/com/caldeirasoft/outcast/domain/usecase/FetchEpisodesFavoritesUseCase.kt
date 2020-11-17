package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.EpisodeSummary
import com.caldeirasoft.outcast.domain.repository.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchEpisodesFavoritesUseCase @Inject constructor(
    val episodeRepository: EpisodeRepository)
    : UseCase<FetchEpisodesFavoritesUseCase.Params, List<EpisodeSummary>> {
    override fun invoke(params: Params): Flow<List<EpisodeSummary>> =
        when {
            params.podcastId != null -> episodeRepository.fetchEpisodesFavoritesByPodcastId(params.podcastId)
            else -> episodeRepository.fetchEpisodesFavorites()
        }

    data class Params(val podcastId: Long?)
}