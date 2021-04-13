package com.caldeirasoft.outcast.data.util

import androidx.paging.PagingState
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StorePage
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

class StoreDataPagingSource(
    override val scope: CoroutineScope,
    private val loadDataFromNetwork: suspend () -> StorePage,
    override val getStoreItems: suspend (List<Long>, String, StorePage?) -> List<StoreItem>,
    private val dataLoadedCallback: ((StorePage) -> Unit)?
) : BasePagingSource<StoreItem>(), StorePagingSource
{
    override suspend fun loadFromNetwork(params: LoadParams<Int>): List<StoreItem> {
        Timber.d("DBG - got new Grouping data : use it to Paging")
        val storePage = loadDataFromNetwork()
        Timber.d("DBG - got new Grouping data : use it to Paging (2)")
        dataLoadedCallback?.invoke(storePage)
        val items = mutableListOf<StoreItem>()
        when {
            storePage.isMultiRoom -> {
                val endPosition =
                    Integer.min(
                        storePage.storeList.size,
                        position + params.loadSize
                    )
                items +=
                    if (position < endPosition)
                        getCollections(position, endPosition, storePage)
                    else emptyList()
            }
            else -> {
                val ids = storePage.storeIds
                val endPosition = Integer.min(ids.size, position + params.loadSize)
                val subset = ids.subList(position, endPosition)
                items += getItemsFromIds(subset, storePage)
            }
        }
        Timber.d("DBG - return items to Paging")
        return items
    }

    override fun getRefreshKey(state: PagingState<Int, StoreItem>): Int? =
        state.anchorPosition
}