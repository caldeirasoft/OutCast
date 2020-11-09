package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.EpisodeSummary
import com.caldeirasoft.outcast.domain.repository.EpisodeRepository
import com.caldeirasoft.outcast.domain.repository.InboxRepository
import com.caldeirasoft.outcast.domain.repository.QueueRepository
import com.caldeirasoft.outcast.domain.usecase.base.UseCase
import com.caldeirasoft.outcast.domain.models.Genre
import kotlinx.coroutines.flow.Flow


class FetchInboxUseCase(val inboxRepository: InboxRepository)
    : UseCase<FetchInboxUseCase.Params, List<EpisodeSummary>> {
    override fun invoke(params: Params): Flow<List<EpisodeSummary>> =
        when {
            params.genreId != null -> inboxRepository.fetchEpisodesByGenre(params.genreId)
            else -> inboxRepository.fetchEpisodes()
        }

    data class Params(val genreId: Int?)
}