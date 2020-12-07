package com.caldeirasoft.outcast.data.util

import android.util.Log
import androidx.paging.PagingSource
import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.repository.DataStoreRepository
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single

class TopChartsPagingSource(
    val storeDataCollection: StoreCollectionPodcastsEpisodes,
    val storeDataLookup: StorePage,
    val getStoreItemsUseCase: GetStoreItemsUseCase,
) : PagingSource<Int, StoreItem>()
{
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreItem> {
        val startPosition = params.key ?: 0
        val items = mutableListOf<StoreItem>()
        val ids = storeDataCollection.itemsIds
        val endPosition = Integer.min(ids.size - 1, startPosition + params.loadSize)
        val subset = ids.subList(startPosition, endPosition)
        items += getStoreItemsUseCase.execute(subset, storeDataLookup)

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