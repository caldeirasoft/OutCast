package com.caldeirasoft.outcast.data.util

import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StorePage
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

class StoreChartsPagingSource(
    override val scope: CoroutineScope,
    val storeFront: String,
    private val loadDataFromNetwork: suspend () -> List<Long>,
    override val getStoreItems: suspend (List<Long>, String, StorePage?) -> List<StoreItem>,
) : BasePagingSource<StoreItem>(), StorePagingSource {

    override suspend fun loadFromNetwork(params: LoadParams<Int>): List<StoreItem> {
        Timber.d("DBG - got new Grouping data : use it to Paging")
        val ids = loadDataFromNetwork()
        Timber.d("DBG - got new Grouping data : use it to Paging (2)")
        val endPosition = Integer.min(ids.size, position + params.loadSize)
        val subset = ids.subList(position, endPosition)
        val items = getItemsFromIds(subset, storeFront, null)
        Timber.d("DBG - return items to Paging")
        return items
    }
}