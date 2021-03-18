package com.caldeirasoft.outcast.data.util

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PodcastRemoteMediator(
    val podcast: Podcast,
    val storeFront: String,
    val storeRepository: StoreRepository,
    val libraryRepository: LibraryRepository,
) : RemoteMediator<Int, Episode>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Episode>,
    ): MediatorResult {
        var doUpdate = true
        when (loadType) {
            LoadType.REFRESH -> null
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
            }
        }
        try {
            val items = storeRepository.getListStoreItemDataAsync(listOf(podcast.podcastId),
                storeFront = storeFront,
                storePage = null)
            if (items.isNotEmpty()) {
                val podcastLookup = items.firstOrNull() as? StorePodcast
                if (podcastLookup != null)
                    doUpdate =
                        libraryRepository.doesPodcastNeedUpdate(podcastId = podcast.podcastId,
                            podcastLookup = podcastLookup)

                if (doUpdate) {
                    // get episodes
                    val podcastPageRemote =
                        storeRepository.getPodcastDataAsync(url = podcast.url,
                            storeFront = storeFront)
                    libraryRepository.updatePodcastAndEpisodes(podcastPageRemote)
                }
            }
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}