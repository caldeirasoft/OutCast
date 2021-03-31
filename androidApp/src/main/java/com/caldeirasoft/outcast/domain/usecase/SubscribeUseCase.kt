package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.caldeirasoft.outcast.domain.models.PodcastPage
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SubscribeUseCase(
    private val libraryRepository: LibraryRepository,
    private val storeRepository: StoreRepository,
) {
    fun execute(podcastPage: PodcastPage, newEpisodesAction: NewEpisodesAction): Flow<Boolean> =
        flow {
            delay(1500)
            libraryRepository.updatePodcastAndEpisodes(podcastPage)
            libraryRepository.subscribeToPodcast(
                podcastId = podcastPage.podcast.podcastId,
                newEpisodeAction = newEpisodesAction)
            emit(true)
        }

    fun execute(storePodcast: StorePodcast): Flow<Boolean> = flow {
        // fetch remote podcast data
        val podcastPage = storeRepository.getPodcastDataAsync(url = storePodcast.url,
            storeFront = storePodcast.storeFront)
        libraryRepository.updatePodcastAndEpisodes(podcastPage)
        libraryRepository.subscribeToPodcast(
            podcastId = podcastPage.podcast.podcastId,
            newEpisodeAction = NewEpisodesAction.INBOX)
        emit(true)
    }
}