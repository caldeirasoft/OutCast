package com.caldeirasoft.outcast.data.util

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionItems
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import kotlinx.coroutines.CoroutineScope

class PodcastRelatedPagingSource(
    override val scope: CoroutineScope,
    val otherPodcasts: StorePageWithCollection,
    val getStoreItemsUseCase: GetStoreItemsUseCase
) : PagingSource<Int, StoreItem>(), StorePagingSource
{
    override val getStoreItems: suspend (List<Long>, String, StorePage?) -> List<StoreItem> =
        getStoreItemsUseCase::execute

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreItem> {
        val startPosition = params.key ?: 0
        val items = mutableListOf<StoreItem>()

        val endPosition =
            Integer.min(
                otherPodcasts.storeList.size,
                startPosition + params.loadSize
            )
        items +=
            if (startPosition < endPosition)
                getCollections(startPosition, endPosition, otherPodcasts)
            else emptyList()

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

    override suspend fun getCollections(
        startPosition: Int,
        endPosition: Int,
        storePage: StorePageWithCollection) : List<StoreCollection>
    {
        val ids: MutableSet<Long> = mutableSetOf()
        val subList = storePage
            .storeList
            .subList(startPosition, endPosition)

        subList
            .filterIsInstance<StoreCollectionItems>()
            .flatMap { it.itemsIds }
            .let { list -> ids.addAll(list) }

        val fetchItems = getItemsFromIds(ids.toList(), storePage)
        val storeItemsMap = fetchItems
            .filterIsInstance<StoreItemWithArtwork>()
            .map { it.id to it }
            .toMap()

        val itemsSequence: Sequence<StoreCollection> = sequence {
            for (i in startPosition until endPosition) {
                when (val collection = storePage.storeList[i])
                {
                    is StoreCollectionItems -> {
                        collection.items +=
                            collection.itemsIds
                                .filter { storeItemsMap.contains(it) }
                                .mapNotNull { storeItemsMap[it] }
                        yield(collection)
                    }
                }
            }
        }
        return itemsSequence.toList()
    }

    override fun getRefreshKey(state: PagingState<Int, StoreItem>): Int? =
        state.anchorPosition
}