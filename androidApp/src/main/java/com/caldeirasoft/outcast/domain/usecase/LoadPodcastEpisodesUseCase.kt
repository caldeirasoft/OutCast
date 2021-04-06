package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository

class LoadPodcastEpisodesUseCase(
    private val libraryRepository: LibraryRepository,
) {
    fun execute(podcastId: Long) =
        libraryRepository.loadEpisodesByPodcastId(podcastId = podcastId)
}