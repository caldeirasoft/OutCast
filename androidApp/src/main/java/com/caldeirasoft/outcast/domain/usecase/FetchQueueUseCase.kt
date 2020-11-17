package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.EpisodeSummary
import com.caldeirasoft.outcast.domain.repository.QueueRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchQueueUseCase @Inject constructor(val queueRepository: QueueRepository)
    : UseCaseWithoutInput<List<EpisodeSummary>> {
    override fun invoke(): Flow<List<EpisodeSummary>> =
        queueRepository.fetchQueue()
}