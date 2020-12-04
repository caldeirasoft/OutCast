package com.caldeirasoft.outcast.ui.screen.storedirectory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDirectoryUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf

@ExperimentalCoroutinesApi
class DiscoverViewModel(
    private val fetchStoreDirectoryUseCase: FetchStoreDirectoryUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    private val getStoreItemsUseCase: GetStoreItemsUseCase,
) : ViewModel() {

    private val storeFront = fetchStoreFrontUseCase.getStoreFront()

    val discover: Flow<PagingData<StoreItem>> = flowOf(
        storeFront.flatMapLatest { getDirectoryPagedList(it) }
    ).flattenMerge()
        .cachedIn(viewModelScope)

    private fun getDirectoryPagedList(storeFront: String): Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 3,
                enablePlaceholders = false,
                maxSize = 100,
                prefetchDistance = 2
            )
        ) {
            object : StoreDataPagingSource(getStoreItemsUseCase) {
                override fun getStoreData(): Flow<StoreData> =
                    fetchStoreDirectoryUseCase.execute(storeFront)
            }
        }.flow
}


