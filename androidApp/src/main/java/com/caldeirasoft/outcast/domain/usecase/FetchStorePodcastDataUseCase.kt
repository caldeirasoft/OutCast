package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import javax.inject.Inject
import kotlin.time.hours

class FetchStorePodcastDataUseCase @Inject constructor(
    val podcastsRepository: PodcastsRepository,
    val storeRepository: StoreRepository,
    val dataStoreRepository: DataStoreRepository,
) {
    fun execute(podcast: Podcast, storeFront: String): Flow<Resource<Podcast>> =
        flow<Resource<Podcast>> {
            val cachedData = loadFromDb(podcast.feedUrl).firstOrNull()
            if (shouldFetch(cachedData, storeFront)) {
                updatePodcast(podcast)
                emitAll(loadFromDb(podcast.feedUrl)
                    .filterNotNull()
                    .map {
                        Resource.Success(it)
                    })

            } else {
                emitAll(loadFromDb(podcast.feedUrl)
                    .filterNotNull()
                    .map {
                        Resource.Success(it)
                    })
            }
        }.onStart { emit(Resource.Loading()) }
            .catch { emit(Resource.Error(it)) }

    private fun loadFromDb(feedUrl: String): Flow<Podcast?> =
        podcastsRepository.loadPodcast(feedUrl)

    private suspend fun shouldFetch(podcast: Podcast?, storeFront: String): Boolean =
        podcast?.let {
            var needUpdate = false
            val now = Clock.System.now()
            if (now - it.updatedAt > 1.hours) {
                val podcastId = podcast.podcastId
                if (podcastId != null) {
                    val items = storeRepository.getListStoreItemDataAsync(
                        lookupIds = listOf(podcastId),
                        storeFront = storeFront,
                        storePage = null)

                    if (items.isNotEmpty()) {
                        val podcastLookup = items.firstOrNull() as? StorePodcast
                        if (podcastLookup != null)
                            needUpdate = podcastsRepository.updatePodcastReleaseDate(
                                feedUrl = podcast.feedUrl,
                                podcastLookup = podcastLookup)
                    }
                } else needUpdate = true
            }
            needUpdate
        } ?: true

    private suspend fun updatePodcast(podcast: Podcast) =
        podcastsRepository.updatePodcast(podcast.feedUrl, podcast)
}