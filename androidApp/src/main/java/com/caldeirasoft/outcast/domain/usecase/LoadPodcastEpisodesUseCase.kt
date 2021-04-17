package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository

class LoadPodcastEpisodesUseCase(
    private val libraryRepository: LibraryRepository,
) {
    fun execute(feedUrl: String) =
        libraryRepository.loadEpisodesByFeedUrl(feedUrl = feedUrl)
}