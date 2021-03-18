package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.db.Podcast

class LoadPodcastUseCase(val podcastRepository: LibraryRepository)
    : FlowUseCase<Long, Podcast?> {
    override fun execute(param: Long) =
        podcastRepository.loadPodcast(param)
}