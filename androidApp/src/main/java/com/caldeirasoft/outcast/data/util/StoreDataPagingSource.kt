package com.caldeirasoft.outcast.data.util

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.interfaces.StorePageWithCollection
import com.caldeirasoft.outcast.domain.models.store.StoreRoomPage
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onError
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class StoreDataPagingSource(
    override val scope: CoroutineScope,
    inline val dataFlow: () -> Flow<Resource?>
) : PagingSource<Int, StoreItem>(), StorePagingSource, KoinComponent
{
    override val getStoreItemsUseCase: GetStoreItemsUseCase by inject()

    private val storeDataFlow: StateFlow<Resource?> =
        dataFlow()
            .stateIn(scope, SharingStarted.Eagerly, null)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoreItem> {
        val flow = storeDataFlow
        val flowNotNull = flow.filterNotNull()
        val storeResource = flowNotNull.filter { it !is Resource.Loading }
        val storeResourceFirst = storeResource.first()
        val startPosition = params.key ?: 0
        storeResourceFirst
            .onSuccess<StorePage> { storePage ->
                val items = mutableListOf<StoreItem>()
                when (storePage) {
                    is StorePageWithCollection -> {
                        val endPosition =
                            Integer.min(
                                storePage.storeList.size,
                                startPosition + params.loadSize
                            )
                        items +=
                            if (startPosition < endPosition)
                                getCollections(startPosition, endPosition, storePage)
                            else emptyList()
                    }
                    is StoreRoomPage -> {
                        val ids = storePage.storeRoom.storeIds
                        val endPosition = Integer.min(ids.size, startPosition + params.loadSize)
                        val subset = ids.subList(startPosition, endPosition)
                        items += getItemsFromIds(subset, storePage)
                    }
                }

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
            .onError {
                return LoadResult.Error(
                    throwable = it
                )
            }

        return LoadResult.Error(throwable = Exception("error loading"))
    }

    override fun getRefreshKey(state: PagingState<Int, StoreItem>): Int? =
        state.anchorPosition
}