package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.db.Podcast

class LoadPodcastEpisodesUseCase(
    private val libraryRepository: LibraryRepository,
) {
    fun execute(podcast: Podcast) =
        libraryRepository.loadEpisodesByPodcastId(podcastId = podcast.podcastId)
}