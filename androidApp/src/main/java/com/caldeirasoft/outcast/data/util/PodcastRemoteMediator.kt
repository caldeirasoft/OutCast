package com.caldeirasoft.outcast.data.util

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import retrofit2.HttpException
import java.io.IOException
import kotlin.time.hours

@OptIn(ExperimentalPagingApi::class)
class PodcastRemoteMediator(
    val podcastId: Long,
    val podcastUrl: String,
    val storeFront: String,
    val storeRepository: StoreRepository,
    val libraryRepository: LibraryRepository,
) : RemoteMediator<Int, Episode>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Episode>,
    ): MediatorResult {
        var needUpdate = true
        when (loadType) {
            LoadType.REFRESH -> null
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
            }
        }
        try {
            val items = storeRepository.getListStoreItemDataAsync(listOf(podcastId),
                storeFront = storeFront,
                storePage = null)
            if (items.isNotEmpty()) {
                val podcastLookup = items.firstOrNull() as? StorePodcast
                if (podcastLookup != null)
                    needUpdate = libraryRepository.doesPodcastNeedUpdate(
                        podcastId = podcastId,
                        podcastLookup = podcastLookup)

                if (needUpdate) {
                    // get episodes
                    val podcastPageRemote = storeRepository.getPodcastDataAsync(
                        url = podcastUrl,
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

    override suspend fun initialize(): InitializeAction {
        val podcastFromDb = libraryRepository.loadPodcast(podcastId = podcastId).firstOrNull()
        val now = Clock.System.now()
        return if ((podcastFromDb != null) && (now - podcastFromDb.updatedAt <= 1.hours)) {
            // Cached data is up-to-date, so there is no need to re-fetch
            // from the network.
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // Need to refresh cached data from network; returning
            // LAUNCH_INITIAL_REFRESH here will also block RemoteMediator's
            // APPEND and PREPEND from running until REFRESH succeeds.
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }
}