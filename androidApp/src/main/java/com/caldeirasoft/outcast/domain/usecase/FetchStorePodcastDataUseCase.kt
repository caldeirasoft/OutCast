package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.common.PodcastPreferenceKeys
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import java.lang.Integer.parseInt
import kotlin.time.hours

class FetchStorePodcastDataUseCase constructor(
    val storeRepository: StoreRepository,
    val libraryRepository: LibraryRepository,
    val dataStoreRepository: DataStoreRepository,
) {
    fun execute(podcast: Podcast, storeFront: String): Flow<Resource<Podcast>> =
        flow<Resource<Podcast>> {
            val cachedData = loadFromDb(podcast.podcastId).firstOrNull()
            if (shouldFetch(cachedData, storeFront)) {
                fetchFromRemote(podcast, storeFront).let { remoteData ->
                    saveRemoteData(cachedData, remoteData)
                    emitAll(loadFromDb(podcast.podcastId)
                        .filterNotNull()
                        .map {
                            Resource.Success(it)
                        })
                }
            } else {
                emitAll(loadFromDb(podcast.podcastId)
                    .filterNotNull()
                    .map {
                        Resource.Success(it)
                    })
            }
        }.onStart { emit(Resource.Loading()) }
            .catch { emit(Resource.Error(it)) }

    private fun loadFromDb(podcastId: Long): Flow<Podcast?> =
        libraryRepository.loadPodcast(podcastId)

    private suspend fun shouldFetch(podcast: Podcast?, storeFront: String): Boolean =
        podcast?.let {
            var needUpdate = false
            val now = Clock.System.now()
            if (now - it.updatedAt > 1.hours) {
                val items = storeRepository.getListStoreItemDataAsync(
                    lookupIds = listOf(it.podcastId),
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

    private suspend fun fetchFromRemote(podcast: Podcast, storeFront: String): StorePodcast =
        storeRepository.getPodcastDataAsync(podcast.url, storeFront)

    private suspend fun saveRemoteData(cachedPodcast: Podcast?, storePodcast: StorePodcast) {
        val podcastReleaseDateTime = cachedPodcast?.releaseDateTime
        libraryRepository.updatePodcastAndEpisodes(storePodcast)
        cachedPodcast?.let {
            val podcastPreferenceKeys = PodcastPreferenceKeys(podcastId = cachedPodcast.podcastId)
            val settingsNewEpisodes =
                dataStoreRepository.dataStore.data
                    .map { preferences -> preferences[podcastPreferenceKeys.newEpisodes] }
                    .firstOrNull()

            val settingsEpisodeLimit: Int =
                dataStoreRepository.dataStore.data
                    .map { preferences -> preferences[podcastPreferenceKeys.episodeLimit] }
                    .firstOrNull()
                    ?.let { parseInt(it) }
                    ?: 0

            // add recent episodes to queue / inbox
            when (settingsNewEpisodes) {
                NewEpisodesAction.INBOX.name -> {
                    libraryRepository.addRecentEpisodesIntoInbox(
                        cachedPodcast.podcastId,
                        cachedPodcast.releaseDateTime)
                    if (settingsEpisodeLimit != 0) {
                        libraryRepository.updateInboxEpisodeLimit(
                            cachedPodcast.podcastId,
                            settingsEpisodeLimit
                        )
                    }
                }
                NewEpisodesAction.QUEUE_FIRST.name ->
                    libraryRepository.addRecentEpisodesIntoQueueFirst(
                        cachedPodcast.podcastId,
                        cachedPodcast.releaseDateTime)
                NewEpisodesAction.QUEUE_LAST.name ->
                    libraryRepository.addRecentEpisodesIntoQueueLast(
                        cachedPodcast.podcastId,
                        cachedPodcast.releaseDateTime)

            }
        }
    }
    //storeRepository.getPodcastDataAsync(url, storeFront)
}