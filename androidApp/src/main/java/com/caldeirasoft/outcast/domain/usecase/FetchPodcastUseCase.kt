package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.PodcastRepository
import com.caldeirasoft.outcast.domain.models.Podcast

class FetchPodcastUseCase(val podcastRepository: PodcastRepository)
    : FlowUseCase<Long, Podcast> {
    override fun execute(param: Long) =
        podcastRepository.getPodcast(param)
}