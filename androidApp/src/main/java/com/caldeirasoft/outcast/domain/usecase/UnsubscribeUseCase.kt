package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UnsubscribeUseCase constructor(val podcastRepository: LibraryRepository):
    FlowUseCase<UnsubscribeUseCase.Params, Unit> {

    override fun execute(params: Params): Flow<Unit> = flow {
        podcastRepository.unsubscribeFromPodcast(podcastId = params.podcastId)
    }

    data class Params(val podcastId: Long)
}