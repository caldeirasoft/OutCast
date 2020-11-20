package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.repository.EpisodeRepository
import com.caldeirasoft.outcast.domain.enum.PodcastEpisodesFilterType
import com.caldeirasoft.outcast.domain.models.EpisodeSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchEpisodesFromPodcastUseCase @Inject constructor(
    val episodeRepository: EpisodeRepository)
    : FlowUseCase<FetchEpisodesFromPodcastUseCase.Params, List<EpisodeSummary>> {
    override fun execute(param: Params) =
        when (param.filter) {
            PodcastEpisodesFilterType.QUEUE -> episodeRepository.fetchEpisodesFromQueueByPodcastId(param.podcastId)
            PodcastEpisodesFilterType.INBOX -> episodeRepository.fetchEpisodesFromInboxByPodcastId(param.podcastId)
            PodcastEpisodesFilterType.FAVORITES -> episodeRepository.fetchEpisodesFavoritesByPodcastId(param.podcastId)
            PodcastEpisodesFilterType.HISTORY -> episodeRepository.fetchEpisodesHistoryByPodcastId(param.podcastId)
            else -> episodeRepository.fetchEpisodesByPodcastId(param.podcastId)
        }

    data class Params(
        val podcastId: Long,
        val filter: PodcastEpisodesFilterType?)
}