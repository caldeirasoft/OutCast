package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.QueueRepository
import com.caldeirasoft.outcast.db.Episode
import kotlinx.coroutines.flow.Flow

class FetchQueueUseCase(val queueRepository: QueueRepository) :
    FlowUseCaseWithoutParams<List<Episode>> {
    override fun execute(): Flow<List<Episode>> =
        queueRepository.fetchQueue()
}