package com.caldeirasoft.outcast.ui.screen.store.directory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreTopChartsIdsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
abstract class StoreCollectionsViewModel<T : StorePage>(
    //protected val getStoreItemsUseCase: GetStoreItemsUseCase,
    //protected val fetchStoreGroupingUseCase: FetchStoreGroupingUseCase,
    protected val fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    protected val fetchStoreTopChartsIdsUseCase: FetchStoreTopChartsIdsUseCase
) : ViewModel() {
    // paging source
    protected var pagingSource: PagingSource<Int, StoreItem>? = null
    // storefront
    protected val storeFront = fetchStoreFrontUseCase.getStoreFront()
    // store resource data
    protected val storeResourceData = MutableStateFlow<Resource>(Resource.Loading)

    // store data
    protected val storeData: StateFlow<T?> =
        storeResourceData
            .filterIsInstance<Resource.Success<T>>()
            .map { it.data }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // paged list
    val discover: Flow<PagingData<StoreItem>> =
        getStoreDataPagedList()
            .cachedIn(viewModelScope)

    /**
     * getStoreDataPagedList
     */
    protected abstract fun getStoreDataPagedList(): Flow<PagingData<StoreItem>>

    fun refresh() {
        pagingSource?.invalidate()
    }

}