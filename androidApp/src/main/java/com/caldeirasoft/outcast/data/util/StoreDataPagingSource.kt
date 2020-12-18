package com.caldeirasoft.outcast.data.util

import android.util.Log
import androidx.paging.PagingSource
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionPodcastIds
import com.caldeirasoft.outcast.domain.repository.DataStoreRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDataUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.checkType
import com.caldeirasoft.outcast.domain.util.tryCast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StoreDataPagingSource(
    val scope: CoroutineScope,
    inline val dataFlow: () -> Flow<StoreData>
) : PagingSource<Int, StoreItem>(), KoinComponent
{
    private val getStoreItemsUseCase: GetStoreItemsUseCase by inject()

    private val storeDataFlow: StateFlow<StoreData?> =
        dataFlow()
            .stateIn(scope, SharingStarted.Eagerly, null)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreItem> {
        val flow = storeDataFlow
        val flowNotNull = flow.filterNotNull()
        val storeData = flowNotNull.first()
        val startPosition = params.key ?: 0
        val items = mutableListOf<StoreItem>()
        when (storeData) {
            is StoreDataWithCollections -> {
                val endPosition =
                    Integer.min(
                        storeData.storeList.size - 1,
                        startPosition + params.loadSize
                    )
                items +=
                    if (startPosition < endPosition)
                        getCollections(startPosition, endPosition, storeData)
                    else emptyList()
            }
            is StoreRoomPage -> {
                val ids = storeData.storeIds
                val endPosition = Integer.min(ids.size - 1, startPosition + params.loadSize)
                val subset = ids.subList(startPosition, endPosition)
                items += getItemsFromIds(subset, storeData)
            }
        }

        val prevKey = if (startPosition > 0) startPosition - items.size else null
        val nextKey = if (items.isNotEmpty() && items.size == params.loadSize)
            startPosition + items.size else null
        Log.d("params", "$prevKey-$nextKey-${params.loadSize}-${items.size}")
        return LoadResult.Page(
            data = items,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }

    protected suspend fun getItemsFromIds(
        ids: List<Long>,
        storeData: StorePage
    ): List<StoreItem> =
        scope.async {
            getStoreItemsUseCase.execute(ids, storeData.storeFront, storeData)
        }.await()

    protected suspend fun getCollections(
        startPosition: Int,
        endPosition: Int,
        storeData: StoreDataWithCollections) : List<StoreCollection>
    {
        val ids: MutableSet<Long> = mutableSetOf()
        val subList = storeData
            .storeList
            .subList(startPosition, endPosition)

        subList
            .filterIsInstance<StoreCollectionPodcastIds>()
            .flatMap { it.itemsIds.take(8) }
            .let { list -> ids.addAll(list) }

        subList
            .filterIsInstance<StoreCollectionEpisodeIds>()
            .flatMap { it.itemsIds.take(8) }
            .let { list -> ids.addAll(list) }

        subList.filterIsInstance<StoreCollectionChartsIds>()
            .flatMap { it.topPodcastsIds + it.topEpisodesIds }
            .let { list -> ids.addAll(list) }

        val fetchItems = getItemsFromIds(ids.toList(), storeData)
        val podcastItemsMap = fetchItems
            .filterIsInstance<StorePodcast>()
            .map { it.id to it }
            .toMap()
        val episodeItemsMap = fetchItems
            .filterIsInstance<StoreEpisode>()
            .map { it.id to it }
            .toMap()

        val itemsSequence: Sequence<StoreCollection> = sequence {
            for (i in startPosition..endPosition) {
                when (val collection = storeData.storeList[i])
                {
                    is StoreCollectionFeatured,
                    is StoreCollectionRooms -> yield(collection)
                    is StoreCollectionChartsIds -> {
                        yield(
                            StoreCollectionCharts(
                                genreId = collection.genreId,
                                topPodcasts = collection.topPodcastsIds
                                    .filter { podcastItemsMap.contains(it) }
                                    .map { podcastItemsMap[it] }
                                    .filterNotNull(),
                                topEpisodes = collection.topEpisodesIds
                                    .filter { episodeItemsMap.contains(it) }
                                    .map { episodeItemsMap[it] }
                                    .filterNotNull(),
                                storeFront = collection.storeFront
                            )
                        )
                    }
                    is StoreCollectionPodcastIds -> {
                        yield(
                            StoreCollectionPodcasts(
                                label = collection.label,
                                url = collection.url,
                                storeFront = collection.storeFront,
                                itemsIds = collection.itemsIds,
                                items = collection.itemsIds
                                    .filter { podcastItemsMap.contains(it) }
                                    .map { podcastItemsMap[it] }
                                    .filterNotNull()
                            )
                        )
                    }
                    is StoreCollectionEpisodeIds -> {
                        yield(
                            StoreCollectionEpisodes(
                                label = collection.label,
                                url = collection.url,
                                storeFront = collection.storeFront,
                                itemsIds = collection.itemsIds,
                                items = collection.itemsIds
                                    .filter { episodeItemsMap.contains(it) }
                                    .map { episodeItemsMap[it] }
                                    .filterNotNull()
                            )
                        )


                    }
                }
            }
        }
        return itemsSequence.toList()
    }
}