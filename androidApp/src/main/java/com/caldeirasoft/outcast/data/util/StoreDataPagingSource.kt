package com.caldeirasoft.outcast.data.util

import androidx.paging.PagingState
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.ui.screen.base.StoreUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class StoreDataPagingSource(
    private val loadDataFromNetwork: suspend () -> StoreData,
    override val getStoreItems: suspend (List<Long>, String, StoreData?) -> List<StoreItem>,
    private val dataLoadedCallback: ((StoreData) -> Unit)?,
    override val itemsLimit: Int? = 15
) : BasePagingSource<StoreUiModel>(), StorePagingSource {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    override suspend fun loadFromNetwork(params: LoadParams<Int>): List<StoreUiModel> {
        val storeData = loadDataFromNetwork()
        dataLoadedCallback?.invoke(storeData)
        val items = mutableListOf<StoreUiModel>()
        when {
            storeData.isMultiRoom -> {
                val endPosition =
                    Integer.min(
                        storeData.storeList.size,
                        position + params.loadSize
                    )
                items +=
                    if (position < endPosition)
                        getCollections(position, endPosition, storeData)
                    else emptyList()
            }
            else -> {
                val ids = storeData.storeIds
                val endPosition = Integer.min(ids.size, position + params.loadSize)
                val subset = ids.subList(position, endPosition)
                items += getItemsFromIds(subset, storeData)
            }
        }
        Timber.d("DBG - return items to Paging")
        return items
    }

    override fun getRefreshKey(state: PagingState<Int, StoreUiModel>): Int? =
        state.anchorPosition
}