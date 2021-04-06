package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SubscribeUseCase(
    private val libraryRepository: LibraryRepository,
    private val storeRepository: StoreRepository,
) {
    fun execute(podcastId: Long, newEpisodesAction: NewEpisodesAction): Flow<Boolean> =
        flow {
            libraryRepository.subscribeToPodcast(
                podcastId = podcastId,
                newEpisodeAction = newEpisodesAction)
            emit(true)
        }

    fun execute(storePodcast: StorePodcast): Flow<Boolean> = flow {
        // fetch remote podcast data
        val storePodcast = storeRepository.getPodcastDataAsync(
            url = storePodcast.url,
            storeFront = storePodcast.storeFront)
        libraryRepository.updatePodcastAndEpisodes(storePodcast)
        libraryRepository.subscribeToPodcast(
            podcastId = storePodcast.podcast.podcastId,
            newEpisodeAction = NewEpisodesAction.INBOX)
        emit(true)
    }
}