package com.caldeirasoft.outcast.ui.screen.storedata

import androidx.paging.PagingSource
import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.StoreCollectionFeatured
import com.caldeirasoft.outcast.domain.models.StoreCollectionPodcasts
import com.caldeirasoft.outcast.domain.models.StoreCollectionRooms
import com.caldeirasoft.outcast.domain.models.StoreRoom
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
        val itemsFlow: Flow<StoreCollection> = flow {
            val startPosition = params.key ?: 0
            val endPosition =
                min(storeDataWithCollections.storeList.size - 1, startPosition + params.loadSize)
            if (endPosition > startPosition) {
                for (i in startPosition..endPosition) {
                    val collection = storeDataWithCollections.storeList[i]
                    when (collection) {
                        is StoreCollectionFeatured,
                        is StoreCollectionRooms -> emit(collection)
                        is StoreCollectionPodcastsEpisodes -> {
                            val newCollectionResponse = fetchStoreItemsUseCase
                                .invoke(
                                    FetchStoreItemsUseCase.Params(
                                        collection.itemsIds,
                                        storeDataWithCollections,
                                        storeFront
                                    )
                                )
                                .first()
                            if (newCollectionResponse is NetworkResponse.Success) {
                                collection.items = newCollectionResponse.body
                                emit(collection)
                            }
                        }
                    }
                }
            }
        }

        val items = itemsFlow.toList()
        val prevKey = null
        val nextKey = if (items.isNotEmpty()) items.size else null
        return LoadResult.Page(
            data = items,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}