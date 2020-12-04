package com.caldeirasoft.outcast.data.util

import android.util.Log
import androidx.paging.PagingSource
import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.repository.DataStoreRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single

class StoreDataPagingSource(
    val storeData: StoreData,
    val getStoreItemsUseCase: GetStoreItemsUseCase,
) : PagingSource<Int, StoreItem>()
{
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreItem> {
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
            is StoreRoom -> {
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
    ): List<StoreItem> {
        return getStoreItemsUseCase.execute(ids, storeData)
    }

    protected suspend fun getCollections(
        startPosition: Int,
        endPosition: Int,
        storeData: StoreDataWithCollections) : List<StoreCollection>
    {
        val ids = storeData
            .storeList
            .subList(startPosition, endPosition)
            .filterIsInstance<StoreCollectionPodcastsEpisodes>()
            .flatMap { it.itemsIds.take(8) }
        val fetchItems = getItemsFromIds(ids, storeData)
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
                when (val collection = storeData.storeList[i]) {
                    is StoreCollectionFeatured,
                    is StoreCollectionRooms -> yield(collection)
                    is StoreCollectionPodcasts -> {
                        collection.items =
                            collection.itemsIds
                                .filter { podcastItemsMap.contains(it) }
                                .map { podcastItemsMap[it] }
                                .filterNotNull()
                        yield(collection)
                    }
                    is StoreCollectionEpisodes -> {
                        collection.items =
                            collection.itemsIds
                                .filter { episodeItemsMap.contains(it) }
                                .map { episodeItemsMap[it] }
                                .filterNotNull()
                        yield(collection)
                    }
                }
            }
        }
        return itemsSequence.toList()
    }
}