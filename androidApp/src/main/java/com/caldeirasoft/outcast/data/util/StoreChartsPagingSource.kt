package com.caldeirasoft.outcast.data.util

import android.util.Log
import androidx.paging.PagingSource
import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.store.StoreChart
import com.caldeirasoft.outcast.domain.models.store.StoreTopCharts
import com.caldeirasoft.outcast.domain.usecase.FetchStoreTopChartsIdsUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreTopChartsUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StoreChartsPagingSource(
    val storeChart: StoreChart,
    val storePage: StorePage,
    val scope: CoroutineScope,
) : PagingSource<Int, StoreItem>(), KoinComponent {

    private val fetchStoreTopChartsIdsUseCase: FetchStoreTopChartsIdsUseCase by inject()
    private val getStoreItemsUseCase: GetStoreItemsUseCase by inject()

    private val idsFlow: StateFlow<List<Long>?> =
        getIds()
            .stateIn(scope, SharingStarted.Eagerly, null)

    private fun getIds(): Flow<List<Long>> =
        storeChart.url?.let {
            fetchStoreTopChartsIdsUseCase.execute(
                url = it,
                storeFront = storeChart.storeFront
            )
        } ?: flowOf(storeChart.itemsIds)


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreItem> {
        val startPosition = params.key ?: 0
        val items = mutableListOf<StoreItem>()
        val ids = idsFlow.filterNotNull().first()
        val endPosition = Integer.min(ids.size, startPosition + params.loadSize)
        val subset = ids.subList(startPosition, endPosition)
        items += getStoreItemsUseCase.execute(subset, storeChart.storeFront, storePage)

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
}