package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.EpisodeSummary
import com.caldeirasoft.outcast.domain.repository.EpisodeRepository
import com.caldeirasoft.outcast.domain.repository.QueueRepository
import com.caldeirasoft.outcast.domain.usecase.base.UseCaseWithoutInput
import kotlinx.coroutines.flow.Flow

class FetchQueueUseCase(val queueRepository: QueueRepository)
    : UseCaseWithoutInput<List<EpisodeSummary>> {
    override fun invoke(): Flow<List<EpisodeSummary>> =
        queueRepository.fetchQueue()
}