package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UnsubscribeUseCase constructor(val podcastRepository: LibraryRepository) {

    fun execute(podcastId: Long): Flow<Unit> = flow {
        podcastRepository.unsubscribeFromPodcast(podcastId = podcastId)
        emit(Unit)
    }
}