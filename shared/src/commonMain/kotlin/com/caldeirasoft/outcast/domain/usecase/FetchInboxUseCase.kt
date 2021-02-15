package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.InboxRepository
import com.caldeirasoft.outcast.domain.models.EpisodeSummary
import kotlinx.coroutines.flow.Flow

class FetchInboxUseCase(val inboxRepository: InboxRepository)
    : FlowUseCase<FetchInboxUseCase.Params, List<EpisodeSummary>> {
    override fun execute(param: Params): Flow<List<EpisodeSummary>> =
        when {
            param.genreId != null -> inboxRepository.fetchEpisodesByGenre(param.genreId)
            else -> inboxRepository.fetchEpisodes()
        }

    data class Params(val genreId: Int?)
}