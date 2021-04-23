package com.caldeirasoft.outcast.data.util

import androidx.paging.PagingState
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StorePage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class StoreDataPagingSource(
    private val loadDataFromNetwork: suspend () -> StorePage,
    override val getStoreItems: suspend (List<Long>, String, StorePage?) -> List<StoreItem>,
    private val dataLoadedCallback: ((StorePage) -> Unit)?
) : BasePagingSource<StoreItem>(), StorePagingSource {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    override suspend fun loadFromNetwork(params: LoadParams<Int>): List<StoreItem> {
        val storePage = loadDataFromNetwork()
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