package com.caldeirasoft.outcast.data.util

import androidx.paging.PagingState
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.interfaces.StorePageWithCollection
import com.caldeirasoft.outcast.domain.models.store.StoreRoomPage
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

class StoreDataPagingSource(
    override val scope: CoroutineScope,
    private val storeRepository: StoreRepository,
    private val loadDataFromNetwork: suspend () -> StorePage,
    private val dataLoadedCallback: ((StorePage) -> Unit)?
) : BasePagingSource<StoreItem>(), StorePagingSource
{
    override suspend fun getStoreItems(
        lookupIds: List<Long>,
        storeFront: String,
        storePage: StorePage?
    ): List<StoreItem> =
        storeRepository.getListStoreItemDataAsync(lookupIds, storeFront, storePage)

    override suspend fun loadFromNetwork(params: LoadParams<Int>): List<StoreItem> {
        Timber.d("DBG - got new Grouping data : use it to Paging")
        val storePage = loadDataFromNetwork()
        Timber.d("DBG - got new Grouping data : use it to Paging (2)")
        dataLoadedCallback?.invoke(storePage)
        val items = mutableListOf<StoreItem>()
        when (storePage) {
            is StorePageWithCollection -> {
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
            is StoreRoomPage -> {
                val ids = storePage.storeRoom.storeIds
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