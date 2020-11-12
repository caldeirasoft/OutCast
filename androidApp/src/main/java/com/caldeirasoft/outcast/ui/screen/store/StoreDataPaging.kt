package com.caldeirasoft.outcast.ui.screen.store

import androidx.paging.PagingSource
import com.caldeirasoft.outcast.domain.models.StoreDataWithLookup
import com.caldeirasoft.outcast.domain.models.StoreItem
import com.caldeirasoft.outcast.domain.usecase.FetchStoreItemsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.first
import java.lang.Integer.min

class StoreDataPaging(val fetchStoreItemsUseCase: FetchStoreItemsUseCase) {
    fun getStoreDataItems(
        ids: List<Long>,
        storeDataWithLookup: StoreDataWithLookup,
        storeFront: String)
            : PagingSource<Int, StoreItem> {
        return object : PagingSource<Int, StoreItem>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreItem> {
                val items: MutableList<StoreItem> = mutableListOf()
                val pageNumber = params.key ?: 0
                val startPosition = (params.key ?: 0) * params.loadSize
                val subset = ids.subList(startPosition, min(ids.size - 1, startPosition + params.loadSize))
                if (subset.isNotEmpty()) {
                    val resource = fetchStoreItemsUseCase
                        .invoke(FetchStoreItemsUseCase.Params(subset, storeDataWithLookup, storeFront))
                        .first()
                    if (resource is Resource.Success)
                        items.addAll(resource.data)
                }

                val prevKey = if (pageNumber > 0) pageNumber.minus(1) else null
                val nextKey = if (subset.isNotEmpty()) pageNumber.plus(1) else null
                return LoadResult.Page(
                    data = items,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
        }
    }
}