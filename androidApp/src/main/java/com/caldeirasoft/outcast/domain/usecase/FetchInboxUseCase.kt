package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.EpisodeSummary
import com.caldeirasoft.outcast.domain.repository.InboxRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FetchInboxUseCase @Inject constructor(val inboxRepository: InboxRepository)
    : UseCase<FetchInboxUseCase.Params, List<EpisodeSummary>> {
    override fun invoke(params: Params): Flow<List<EpisodeSummary>> =
        when {
            params.genreId != null -> inboxRepository.fetchEpisodesByGenre(params.genreId)
            else -> inboxRepository.fetchEpisodes()
        }

    data class Params(val genreId: Int?)
}