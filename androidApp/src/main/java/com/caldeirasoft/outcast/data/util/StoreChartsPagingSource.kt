package com.caldeirasoft.outcast.data.util

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class StoreChartsPagingSource(
    val storeFront: String,
    val scope: CoroutineScope,
    val getStoreItemsUseCase: GetStoreItemsUseCase,
    inline val dataFlow: () -> Flow<List<Long>>,
) : PagingSource<Int, StoreItem>(), StorePagingSource {

    override suspend fun getStoreItems(
        lookupIds: List<Long>,
        storeFront: String,
        storePage: StorePage?
    ): List<StoreItem> =
        getStoreItemsUseCase.execute(lookupIds, storeFront, storePage)

    private val idsFlow: StateFlow<List<Long>?> =
        dataFlow()
            .stateIn(scope, SharingStarted.Eagerly, null)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreItem> {
        val flow = idsFlow
        val flowNotNull = flow.filterNotNull()
        val ids = flowNotNull.first()
        val startPosition = params.key ?: 0
        val items = mutableListOf<StoreItem>()
        val endPosition = Integer.min(ids.size, startPosition + params.loadSize)
        val subset = ids.subList(startPosition, endPosition)
        items += getItemsFromIds(subset, storeFront, null)

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

    override fun getRefreshKey(state: PagingState<Int, StoreItem>): Int? =
        state.anchorPosition
}