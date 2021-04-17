package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository

class LoadPodcastUseCase(val podcastRepository: LibraryRepository) {
    fun execute(feedUrl: String) =
        podcastRepository.loadPodcast(feedUrl)
}