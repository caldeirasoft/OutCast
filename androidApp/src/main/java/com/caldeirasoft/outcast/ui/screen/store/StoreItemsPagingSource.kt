package com.caldeirasoft.outcast.ui.screen.store

import androidx.paging.PagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreDataWithLookup
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.StoreRoom
import com.caldeirasoft.outcast.domain.usecase.FetchStoreItemsUseCase
import com.caldeirasoft.outcast.domain.util.NetworkResponse
import kotlinx.coroutines.flow.first
import java.lang.Integer.min

class StoreItemsPagingSource(
    val ids: List<Long>,
    val storeDataWithLookup: StoreDataWithLookup,
    val storeFront: String,
    val fetchStoreItemsUseCase: FetchStoreItemsUseCase)
    : PagingSource<Int, StoreItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreItem> {
        val items: MutableList<StoreItem> = mutableListOf()
        val subset: MutableList<Long> = mutableListOf()
        val pageNumber = params.key ?: 0
        val startPosition = (params.key ?: 0) * params.loadSize
        val endPosition = min(ids.size - 1, startPosition + params.loadSize)
        if (endPosition > startPosition) {
            subset += ids.subList(startPosition, endPosition)
            if (subset.isNotEmpty()) {
                val resource = fetchStoreItemsUseCase
                    .invoke(FetchStoreItemsUseCase.Params(subset, storeDataWithLookup, storeFront))
                    .first()
                if (resource is NetworkResponse.Success)
                    items.addAll(resource.body)
            }
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