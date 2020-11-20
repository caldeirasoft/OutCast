package com.caldeirasoft.outcast.ui.screen.storedata

import androidx.paging.PagingSource
import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.usecase.FetchStoreItemsUseCase
import com.caldeirasoft.outcast.domain.util.NetworkResponse
import kotlinx.coroutines.flow.*
import java.lang.Integer.min

class StoreDataCollectionPagingSource(
    val storeDataWithCollections: StoreDataWithCollections,
    val storeFront: String,
    val fetchStoreItemsUseCase: FetchStoreItemsUseCase)
    : PagingSource<Int, StoreCollection>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreCollection> {
        val startPosition = params.key ?: 0
        val endPosition =
            min(storeDataWithCollections.storeList.size - 1, startPosition + params.loadSize)
        val ids: MutableList<Long> = mutableListOf()
        val items = mutableListOf<StoreCollection>()

        if (endPosition > startPosition) {
            ids += storeDataWithCollections
                .storeList
                .filterIsInstance<StoreCollectionPodcastsEpisodes>()
                .flatMap { it.itemsIds }
            val fetchItems = fetchStoreItemsUseCase
                .invoke(
                    FetchStoreItemsUseCase.Params(
                        ids,
                        storeDataWithCollections,
                        storeFront
                    ))
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
                    val collection = storeDataWithCollections.storeList[i]
                    when (collection) {
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
            items.addAll(itemsSequence.toList())
        }

        val prevKey = null
        val nextKey = if (items.isNotEmpty()) startPosition + items.size else null
        return LoadResult.Page(
            data = items,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}