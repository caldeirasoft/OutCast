package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import javax.inject.Inject

class LoadPodcastUseCase @Inject constructor(val podcastRepository: LibraryRepository) {
    fun execute(feedUrl: String) =
        podcastRepository.loadPodcast(feedUrl)
}