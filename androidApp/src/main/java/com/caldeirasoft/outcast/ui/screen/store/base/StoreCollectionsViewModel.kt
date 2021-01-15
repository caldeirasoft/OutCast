package com.caldeirasoft.outcast.ui.screen.store.directory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreGroupingUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreTopChartsIdsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
@ExperimentalCoroutinesApi
abstract class StoreCollectionsViewModel<T : StorePage> : ViewModel(), KoinComponent {
    private val fetchStoreGroupingUseCase: FetchStoreGroupingUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()
    private val fetchStoreTopChartsIdsUseCase: FetchStoreTopChartsIdsUseCase by inject()
    // paging source
    protected var pagingSource: PagingSource<Int, StoreItem>? = null
    // storefront
    protected val storeFront = fetchStoreFrontUseCase.getStoreFront()
    // store resource data
    protected val storeResourceData: MutableStateFlow<Resource> =
        MutableStateFlow(Resource.Loading)

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

    private fun getStoreDataPagedList(): Flow<PagingData<StoreItem>> =
        Pager(
            /*TODO : change paging config for Room pages */
            PagingConfig(
                pageSize = 5,
                enablePlaceholders = false,
                maxSize = 100,
                prefetchDistance = 2
            )
        ) {
            StoreDataPagingSource(scope = viewModelScope, dataFlow = { getStoreDataFlow() })
                .also {
                    pagingSource = it
                }
        }.flow

    protected abstract fun getStoreDataFlow(): Flow<StoreData>

    fun refresh() {
        pagingSource?.invalidate()
    }

}