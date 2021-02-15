package com.caldeirasoft.outcast.data.util

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.usecase.FetchStoreTopChartsIdsUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class StoreChartsPagingSource(
    val storeFront: String,
    override val scope: CoroutineScope,
    inline val dataFlow: () -> Flow<List<Long>>
) : PagingSource<Int, StoreItem>(), StorePagingSource, KoinComponent {

    override val getStoreItemsUseCase: GetStoreItemsUseCase by inject()

    private val idsFlow: StateFlow<List<Long>?> =
        dataFlow()
            .stateIn(scope, SharingStarted.Eagerly, null)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreItem> {
        //TODO: handle errors : https://developer.android.com/reference/kotlin/androidx/paging/PagingSource
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