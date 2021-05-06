package com.caldeirasoft.outcast.data.util

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import com.caldeirasoft.outcast.domain.models.store.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

interface StorePagingSource
{
    val scope: CoroutineScope
    val getStoreItems: suspend (List<Long>, String, StoreData?) -> List<StoreItem>

    suspend fun getItemsFromIds(
        ids: List<Long>,
        storeData: StoreData
    ): List<StoreItem> = getItemsFromIds(ids, storeData.storeFront, storeData)

    suspend fun getItemsFromIds(
        ids: List<Long>,
        storeFront: String,
        storeData: StoreData?
    ): List<StoreItem> {
        val idsSplit = ids.chunked(20)
        val deferredList = idsSplit.map {
            scope.async(Dispatchers.IO) { getStoreItems(it, storeFront, storeData) }
        }
        val lstOfReturnData = deferredList.awaitAll()
        return lstOfReturnData.flatten()
    }

    suspend fun getCollections(
        startPosition: Int,
        endPosition: Int,
        storeData: StoreData,
    ): List<StoreCollection> {
        val ids: MutableSet<Long> = mutableSetOf()

        storeData
            .storeList
            .subList(startPosition, endPosition)
            .filterIsInstance<StoreCollectionItems>()
            .flatMap { it.itemsIds.take(15) }
            .let { list -> ids.addAll(list) }

        val fetchItems = getItemsFromIds(ids.toList(), storeData)
        val storeItemsMap = fetchItems
            .filterIsInstance<StoreItemArtwork>()
            .map { it.id to it }
            .toMap()

        val itemsSequence: Sequence<StoreCollection> = sequence {
            for (i in startPosition until endPosition) {
                when (val collection = storeData.storeList[i]) {
                    is StoreCollectionFeatured,
                    is StoreCollectionData ->
                        yield(collection)
                    is StoreCollectionItems -> {
                        val newCollection = collection.itemsIds
                            .filter { storeItemsMap.contains(it) }
                            .mapNotNull { storeItemsMap[it] }
                            .let {
                                collection.copy(items = it)
                            }
                        if (newCollection.items.isNotEmpty())
                            yield(newCollection)
                    }
                }
            }
        }
        return itemsSequence.toList()
    }
}