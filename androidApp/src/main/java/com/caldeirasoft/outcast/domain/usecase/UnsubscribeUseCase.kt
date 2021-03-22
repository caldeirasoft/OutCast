package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository

class UnsubscribeUseCase constructor(val podcastRepository: LibraryRepository) {

    fun execute(podcastId: Long) {
        podcastRepository.unsubscribeFromPodcast(podcastId = podcastId)
    }
}