package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.db.InboxDataSource
import com.caldeirasoft.outcast.db.EpisodeSummary
import kotlinx.coroutines.flow.Flow

class FetchInboxUseCase(
    val inboxRepository: InboxDataSource)
    : FlowUseCaseWithoutParams<List<EpisodeSummary>>
{
    override fun execute(): Flow<List<EpisodeSummary>> = inboxRepository.fetchEpisodes()
}