package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.InboxRepository
import com.caldeirasoft.outcast.db.Episode
import kotlinx.coroutines.flow.Flow

class FetchInboxUseCase(
    val inboxRepository: InboxRepository,
) : FlowUseCaseWithoutParams<List<Episode>> {
    override fun execute(): Flow<List<Episode>> = inboxRepository.fetchEpisodes()
}