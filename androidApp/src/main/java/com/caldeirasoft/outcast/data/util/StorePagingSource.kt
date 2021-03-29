package com.caldeirasoft.outcast.data.util

import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.store.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

interface StorePagingSource
{
    val scope: CoroutineScope
    val getStoreItems: suspend (List<Long>, String, StorePage?) -> List<StoreItem>

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
            scope.async(Dispatchers.IO) { getStoreItems(it, storeFront, storePage) }
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
            .filterIsInstance<StoreCollectionPodcasts>()
            .flatMap { it.itemsIds.take(8) }
            .let { list -> ids.addAll(list) }

        subList
            .filterIsInstance<StoreCollectionEpisodes>()
            .flatMap { it.itemsIds.take(12) }
            .let { list -> ids.addAll(list) }

        subList.filterIsInstance<StoreCollectionCharts>()
            .flatMap { it.topPodcastsIds + it.topEpisodesIds }
            .let { list -> ids.addAll(list) }

        val fetchItems = getItemsFromIds(ids.toList(), storePage)
        val storeItemsMap = fetchItems
            .filterIsInstance<StoreItemWithArtwork>()
            .map { it.id to it }
            .toMap()

        val itemsSequence: Sequence<StoreCollection> = sequence {
            for (i in startPosition until endPosition) {
                when (val collection = storePage.storeList[i]) {
                    is StoreCollectionFeatured,
                    is StoreCollectionRooms,
                    ->
                        yield(collection)
                    is StoreCollectionPodcasts -> {
                        collection.itemsIds
                            .filter { storeItemsMap.contains(it) }
                            .mapNotNull { storeItemsMap[it] }
                            .filterIsInstance<StorePodcast>()
                            .let {
                                collection.items += it
                            }
                        yield(collection)
                    }
                    is StoreCollectionEpisodes -> {
                        collection.itemsIds
                            .filter { storeItemsMap.contains(it) }
                            .mapNotNull { storeItemsMap[it] }
                            .filterIsInstance<StoreEpisode>()
                            .let {
                                collection.items += it
                            }
                        yield(collection)
                    }
                    is StoreCollectionCharts -> {
                        collection.topPodcasts +=
                            collection.topPodcastsIds
                                .filter { storeItemsMap.contains(it) }
                                .mapNotNull { storeItemsMap[it] }
                                .filterIsInstance<StorePodcast>()
                        collection.topEpisodes +=
                            collection.topEpisodesIds
                                .filter { storeItemsMap.contains(it) }
                                .mapNotNull { storeItemsMap[it] }
                                .filterIsInstance<StoreEpisode>()
                        yield(collection)
                    }
                }
            }
        }
        return itemsSequence.toList()
    }
}