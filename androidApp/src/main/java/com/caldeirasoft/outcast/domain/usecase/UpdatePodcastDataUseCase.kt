package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import javax.inject.Inject
import kotlin.time.hours

class UpdatePodcastDataUseCase @Inject constructor(
    val podcastsRepository: PodcastsRepository,
    val storeRepository: StoreRepository,
    val dataStoreRepository: DataStoreRepository,
) {
    fun execute(podcast: Podcast): Flow<Boolean> = flow {
        val storeFront = getStoreFront()
        if (shoudUpdate(podcast, storeFront)) {
            podcastsRepository.updatePodcast(podcast.feedUrl, podcast)
        }
        emit(true)
    }

    suspend fun getStoreFront(): String =
        dataStoreRepository.storeCountry
            .map { dataStoreRepository.getCurrentStoreFront(it) }
            .firstOrNull()
            .orEmpty()

    /**
     * Check if podcast should be updated : older than 1 hour
     */
    private suspend fun shoudUpdate(podcast: Podcast, storeFront: String): Boolean {
        var needUpdate = false
        val now = Clock.System.now()
        if (now - podcast.updatedAt > 1.hours) {
            val podcastId = podcast.podcastId
            if (podcastId != null) {
                val items = storeRepository.getListStoreItemDataAsync(
                    lookupIds = listOf(podcastId),
                    storeFront = storeFront,
                    storePage = null
                )

                if (items.isNotEmpty()) {
                    val podcastLookup = items.firstOrNull() as? StorePodcast
                    if (podcastLookup != null)
                        needUpdate = podcastsRepository.updatePodcastReleaseDate(
                            feedUrl = podcast.feedUrl,
                            podcastLookup = podcastLookup
                        )
                }
            } else needUpdate = true
        }
        return needUpdate
    }
}