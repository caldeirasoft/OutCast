package com.caldeirasoft.outcast.data.util

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.db.EpisodeSummary
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PodcastRemoteMediator(
    val podcast: Podcast,
    val storeFront: String,
    val storeRepository: StoreRepository,
    val database: Database,
) : RemoteMediator<Int, EpisodeSummary>()
{
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EpisodeSummary>
    ): MediatorResult {
        val database = storeRepository.database
        var doUpdate = true
            when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> { }
            }
        try {
            val items = storeRepository.getListStoreItemDataAsync(listOf(podcast.podcastId), storeFront = storeFront, storePage = null)
            if (items.isNotEmpty()) {
                val podcastLookup = items.firstOrNull()
                val podcastDb = database.podcastQueries.getById(podcast.podcastId).executeAsOneOrNull()
                if (podcastLookup != null && podcastDb != null && podcastLookup is StorePodcast) {
                    if (podcastLookup.releaseDateTime == podcastDb.releaseDateTime) {
                        database.podcastQueries.updateLastAccess(podcastDb.podcastId)
                        doUpdate = false
                    }
                    else {
                        database.podcastQueries.updateMetadata(podcastLookup.releaseDateTime, podcastLookup.trackCount.toLong(), podcast.podcastId)
                    }
                }
                if (doUpdate) {
                    // get episodes
                    val podcastPageRemote =
                        storeRepository.getPodcastDataAsync(url = podcast.url,
                            storeFront = storeFront)
                    database.transaction {
                        database.podcastQueries.insert(podcastPageRemote.podcast)
                        podcastPageRemote.episodes.onEach {
                            database.episodeQueries.insert(it)
                        }
                    }
                }
            }
            return MediatorResult.Success(endOfPaginationReached = true)
        }
        catch (e: IOException) {
            return MediatorResult.Error(e)
        }
        catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}