package com.caldeirasoft.outcast.data.util

import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.store.StoreTopCharts
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

class StoreChartsPagingSource(
    override val scope: CoroutineScope,
    private val itemType: StoreItemType,
    private val loadDataFromNetwork: suspend () -> StoreTopCharts,
    override val getStoreItems: suspend (List<Long>, String, StorePage?) -> List<StoreItem>,
    private val dataLoadedCallback: ((StoreTopCharts) -> Unit)?,
) : BasePagingSource<StoreItem>(), StorePagingSource {

    override suspend fun loadFromNetwork(params: LoadParams<Int>): List<StoreItem> {
        Timber.d("DBG - got new Grouping data : use it to Paging")
        val storePage = loadDataFromNetwork()
        Timber.d("DBG - got new Grouping data : use it to Paging (2)")
        dataLoadedCallback?.invoke(storePage)
        val ids = when(itemType) {
            StoreItemType.PODCAST -> storePage.storePodcastsIds
            StoreItemType.EPISODE -> storePage.storeEpisodesIds
        }
        val endPosition = Integer.min(ids.size, position + params.loadSize)
        val subset = ids.subList(position, endPosition)
        val items = getItemsFromIds(subset, storePage)
        Timber.d("DBG - return items to Paging")
        return items
    }
}