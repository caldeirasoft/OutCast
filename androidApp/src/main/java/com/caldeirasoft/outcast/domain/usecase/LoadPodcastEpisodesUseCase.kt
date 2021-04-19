package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import javax.inject.Inject

class LoadPodcastEpisodesUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository,
) {
    fun execute(feedUrl: String) =
        libraryRepository.loadEpisodesByFeedUrl(feedUrl = feedUrl)
}