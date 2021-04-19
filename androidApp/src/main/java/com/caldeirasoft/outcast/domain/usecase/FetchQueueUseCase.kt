package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.QueueRepository
import com.caldeirasoft.outcast.db.Episode
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityScoped
class FetchQueueUseCase @Inject constructor(val queueRepository: QueueRepository) :
    FlowUseCaseWithoutParams<List<Episode>> {
    override fun execute(): Flow<List<Episode>> =
        queueRepository.fetchQueue()
}