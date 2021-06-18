package com.caldeirasoft.outcast.data.util

import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.screen.base.StoreUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

interface StorePagingSource
{
    val scope: CoroutineScope
    val getStoreItems: suspend (List<Long>, String, StoreData?) -> List<StoreItem>
    val itemsLimit: Int?

    suspend fun getItemsFromIds(
        ids: List<Long>,
        storeData: StoreData
    ): List<StoreUiModel> = getItemsFromIds(ids, storeData.storeFront, storeData)
        .map {
            StoreUiModel.StoreUiItem(it)
        }

    suspend fun getItemsFromIds(
        ids: List<Long>,
        storeFront: String,
        storeData: StoreData?
    ): List<StoreItem> {
        val idsSplit = ids.chunked(20)
        val deferredList = idsSplit.map {
            scope.async(Dispatchers.IO) {
                getStoreItems(it, storeFront, storeData)
            }
        }
        val lstOfReturnData = deferredList.awaitAll()
        return lstOfReturnData.flatten()
    }

    suspend fun getCollections(
        startPosition: Int,
        endPosition: Int,
        storeData: StoreData,
    ): List<StoreUiModel> {
        val ids: MutableSet<Long> = mutableSetOf()

        storeData
            .storeList
            .subList(startPosition, endPosition)
            .filterIsInstance<StoreCollectionItems>()
            .flatMap {
                itemsLimit
                    ?.let { limit -> it.itemsIds.take(limit) }
                    ?: it.itemsIds
            }
            .let { list -> ids.addAll(list) }

        storeData
            .storeList
            .subList(startPosition, endPosition)
            .filterIsInstance<StoreCollectionEpisodes>()
            .flatMap {
                itemsLimit
                    ?.let { limit -> it.itemsIds.take(limit) }
                    ?: it.itemsIds
            }
            .let { list -> ids.addAll(list) }

        val fetchItems = getItemsFromIds(ids.toList(), storeData.storeFront, storeData)
        val storeItemsMap = fetchItems
            .filterIsInstance<StoreItemArtwork>()
            .map { it.id to it }
            .toMap()

        val itemsSequence: Sequence<StoreUiModel> = sequence {
            for (i in startPosition until endPosition) {
                when (val collection = storeData.storeList[i]) {
                    is StoreCollectionFeatured -> {
                        yield(StoreUiModel.StoreUiItem(collection))
                    }
                    is StoreCollectionData -> {
                        yield(StoreUiModel.TitleItem(collection))
                        yield(StoreUiModel.StoreUiItem(collection))
                    }
                    is StoreCollectionItems -> {
                        val newCollection = collection.itemsIds
                            .filter { storeItemsMap.contains(it) }
                            .mapNotNull { storeItemsMap[it] }
                            .let {
                                collection.copy(items = it)
                            }
                        if (newCollection.items.isNotEmpty()) {
                            yield(StoreUiModel.TitleItem(newCollection))
                            yield(StoreUiModel.StoreUiItem(newCollection))
                        }
                    }
                    is StoreCollectionEpisodes -> {
                        val newCollection = collection.itemsIds
                            .filter { storeItemsMap.contains(it) }
                            .mapNotNull { storeItemsMap[it] }
                            .let {
                                collection.copy(items = it)
                            }
                        if (newCollection.items.isNotEmpty()) {
                            yield(StoreUiModel.TitleItem(collection))
                            yieldAll(newCollection.items.mapIndexed { index, storeItemArtwork ->
                                StoreUiModel.StoreUiItem(
                                    storeItemArtwork,
                                    (index + 1).takeIf { collection.sortByPopularity },
                                ) })
                        }
                    }
                }
            }
        }
        return itemsSequence.toList()
    }
}