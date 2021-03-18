package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.db.InboxDataSource
import com.caldeirasoft.outcast.db.Episode
import kotlinx.coroutines.flow.Flow

class FetchInboxUseCase(
    val inboxRepository: InboxDataSource,
) : FlowUseCaseWithoutParams<List<Episode>> {
    override fun execute(): Flow<List<Episode>> = inboxRepository.fetchEpisodes()
}