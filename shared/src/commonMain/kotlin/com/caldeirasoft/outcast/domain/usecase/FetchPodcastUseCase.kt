package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.domain.models.Podcast
import com.caldeirasoft.outcast.domain.repository.PodcastRepository

class FetchPodcastUseCase(val podcastRepository: PodcastRepository)
    : FlowUseCase<Long, Podcast> {
    override fun execute(param: Long) =
        podcastRepository.getPodcast(param)
}