package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlin.time.hours

class FetchStorePodcastDataUseCase constructor(
    val storeRepository: StoreRepository,
    val libraryRepository: LibraryRepository,
) {
    fun execute(podcast: Podcast, storeFront: String): Flow<Resource<Podcast>> =
        networkBoundResource(
            loadFromDb = {
                libraryRepository.loadPodcast(podcast.podcastId)
            },
            shouldFetch = {
                it?.let {
                    var needUpdate = false
                    val now = Clock.System.now()
                    if (now - it.updatedAt > 1.hours) {
                        val items = storeRepository.getListStoreItemDataAsync(listOf(it.podcastId),
                            storeFront = storeFront,
                            storePage = null)

                        if (items.isNotEmpty()) {
                            val podcastLookup = items.firstOrNull() as? StorePodcast
                            if (podcastLookup != null)
                                needUpdate = libraryRepository.doesPodcastNeedUpdate(
                                    podcastId = it.podcastId,
                                    podcastLookup = podcastLookup)
                        }
                    }
                    needUpdate
                } ?: true
            },
            fetchFromRemote = {
                storeRepository.getPodcastDataAsync(podcast.url, storeFront)
            },
            saveRemoteData = {
                libraryRepository.updatePodcastAndEpisodes(it)
            }
        )
    //storeRepository.getPodcastDataAsync(url, storeFront)
}