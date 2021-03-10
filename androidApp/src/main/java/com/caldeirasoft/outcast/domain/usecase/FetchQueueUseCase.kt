package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.QueueRepository
import com.caldeirasoft.outcast.db.EpisodeSummary
import kotlinx.coroutines.flow.Flow

class FetchQueueUseCase(val queueRepository: QueueRepository)
    : FlowUseCaseWithoutParams<List<EpisodeSummary>> {
    override fun execute(): Flow<List<EpisodeSummary>> =
        queueRepository.fetchQueue()
}