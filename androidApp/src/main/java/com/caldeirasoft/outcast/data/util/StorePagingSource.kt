package com.caldeirasoft.outcast.data.util

import android.util.Log
import androidx.paging.PagingSource
import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onError
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
interface StorePagingSource
{
    val scope: CoroutineScope
    val getStoreItemsUseCase: GetStoreItemsUseCase

    suspend fun getItemsFromIds(
        ids: List<Long>,
        storeData: StorePage
    ): List<StoreItem> = getItemsFromIds(ids, storeData.storeFront, storeData)

    suspend fun getItemsFromIds(
        ids: List<Long>,
        storeFront: String,
        storePage: StorePage?
    ): List<StoreItem> {
        val idsSplit = ids.chunked(20)
        val deferredList = idsSplit.map {
            scope.async(Dispatchers.IO) { getStoreItemsUseCase.execute(it, storeFront, storePage) }
        }
        val lstOfReturnData = deferredList.awaitAll()
        return lstOfReturnData.flatten()
    }

    suspend fun getCollections(
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
            .flatMap { it.itemsIds.take(8) }
            .let { list -> ids.addAll(list) }

        subList
            .filterIsInstance<StoreCollectionTopPodcasts>()
            .flatMap { it.itemsIds.take(12) }
            .let { list -> ids.addAll(list) }

        subList
            .filterIsInstance<StoreCollectionTopEpisodes>()
            .flatMap { it.itemsIds.take(12) }
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
                    is StoreCollectionFeatured,
                    is StoreCollectionRooms,
                    is StoreCollectionGenres ->
                        yield(collection)
                    is StoreCollectionItems -> {
                        collection.itemsIds
                            .filter { storeItemsMap.contains(it) }
                            .mapNotNull { storeItemsMap[it] }
                            .let {
                                collection.items += it
                            }
                        yield(collection)
                    }
                    is StoreCollectionTopPodcasts -> {
                        collection.itemsIds
                            .filter { storeItemsMap.contains(it) }
                            .mapNotNull { storeItemsMap[it] }
                            .filterIsInstance<StorePodcast>()
                            .let {
                                collection.items += it
                            }
                        yield(collection)
                    }
                    is StoreCollectionTopEpisodes -> {
                        collection.itemsIds
                            .filter { storeItemsMap.contains(it) }
                            .mapNotNull { storeItemsMap[it] }
                            .filterIsInstance<StoreEpisode>()
                            .let {
                                collection.items += it
                            }
                        yield(collection)
                    }
                }
            }
        }
        return itemsSequence.toList()
    }
}