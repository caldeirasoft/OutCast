package com.caldeirasoft.outcast.data.util

import android.util.Log
import androidx.paging.PagingSource
import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.store.StoreTopCharts
import com.caldeirasoft.outcast.domain.usecase.FetchStoreTopChartsUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class StoreChartsPagingSource(
    val scope: CoroutineScope,
) : PagingSource<Int, StoreItem>(), KoinComponent
{
    private val getStoreItemsUseCase: GetStoreItemsUseCase by inject()

    abstract fun fetchStoreTopCharts(): Flow<StoreTopCharts>

    abstract fun StoreTopCharts.getIds(): List<Long>

    private val storeChartsFlow: StateFlow<StoreTopCharts?> =
        fetchStoreTopCharts()
            .stateIn(scope, SharingStarted.Eagerly, null)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreItem> {
        val startPosition = params.key ?: 0
        val items = mutableListOf<StoreItem>()
        val storeCharts = storeChartsFlow.filterNotNull().first()
        val ids = storeCharts.getIds()
        val endPosition = Integer.min(ids.size, startPosition + params.loadSize)
        val subset = ids.subList(startPosition, endPosition)
        items += getStoreItemsUseCase.execute(subset, storeCharts.storeFront, storeCharts)

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

    abstract class Podcasts(scope: CoroutineScope) : StoreChartsPagingSource(scope)
    {
        override fun StoreTopCharts.getIds(): List<Long> =
            this.storePodcastsIds
    }

    abstract class Episodes(scope: CoroutineScope) : StoreChartsPagingSource(scope)
    {
        override fun StoreTopCharts.getIds(): List<Long> =
            this.storeEpisodesIds
    }
}