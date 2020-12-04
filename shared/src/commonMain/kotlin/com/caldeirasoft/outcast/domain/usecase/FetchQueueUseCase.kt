package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.EpisodeSummary
import com.caldeirasoft.outcast.domain.repository.QueueRepository
import kotlinx.coroutines.flow.Flow

class FetchQueueUseCase(val queueRepository: QueueRepository)
    : FlowUseCaseWithoutParams<List<EpisodeSummary>> {
    override fun execute(): Flow<List<EpisodeSummary>> =
        queueRepository.fetchQueue()
}