package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.EpisodeRepository
import com.caldeirasoft.outcast.domain.enum.PodcastEpisodesFilterType
import com.caldeirasoft.outcast.domain.models.EpisodeSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchEpisodesFromPodcastUseCase @Inject constructor(
    val episodeRepository: EpisodeRepository)
    : UseCase<FetchEpisodesFromPodcastUseCase.Params, List<EpisodeSummary>> {
    override fun invoke(params: Params): Flow<List<EpisodeSummary>> =
        when (params.filter) {
            PodcastEpisodesFilterType.QUEUE -> episodeRepository.fetchEpisodesFromQueueByPodcastId(params.podcastId)
            PodcastEpisodesFilterType.INBOX -> episodeRepository.fetchEpisodesFromInboxByPodcastId(params.podcastId)
            PodcastEpisodesFilterType.FAVORITES -> episodeRepository.fetchEpisodesFavoritesByPodcastId(params.podcastId)
            PodcastEpisodesFilterType.HISTORY -> episodeRepository.fetchEpisodesHistoryByPodcastId(params.podcastId)
            else -> episodeRepository.fetchEpisodesByPodcastId(params.podcastId)
        }

    data class Params(val podcastId: Long, val filter: PodcastEpisodesFilterType?)
}