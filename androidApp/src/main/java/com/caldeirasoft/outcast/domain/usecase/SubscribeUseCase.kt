package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.caldeirasoft.outcast.domain.models.PodcastPage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SubscribeUseCase(
    private val libraryRepository: LibraryRepository
) {
    fun execute(podcastPage: PodcastPage, newEpisodesAction: NewEpisodesAction): Flow<Boolean> = flow {
        libraryRepository.updatePodcastAndEpisodes(podcastPage)
        libraryRepository.subscribeToPodcast(
            podcastId = podcastPage.podcast.podcastId,
            newEpisodeAction = newEpisodesAction)
        emit(true)
    }
}