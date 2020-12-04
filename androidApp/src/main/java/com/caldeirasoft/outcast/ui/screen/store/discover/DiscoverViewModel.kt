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
import com.caldeirasoft.outcast.domain.models.StoreGroupingData
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDirectoryUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.screen.store.discover.DiscoverViewState
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopChartsViewState
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class DiscoverViewModel(
    private val fetchStoreDirectoryUseCase: FetchStoreDirectoryUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    private val getStoreItemsUseCase: GetStoreItemsUseCase,
) : ViewModel() {

    private val storeFront = fetchStoreFrontUseCase.getStoreFront()
    private val discoverScreenState = MutableStateFlow<ScreenState>(ScreenState.Idle)

    val state: StateFlow<DiscoverViewState> =
        combine(discoverScreenState, storeFront)
        { screenState, storeFront -> DiscoverViewState(screenState) }
            .stateIn(viewModelScope, SharingStarted.Lazily, DiscoverViewState())

    val discover: Flow<PagingData<StoreItem>> = flowOf(
        storeFront
            .flatMapLatest { fetchStoreDirectoryUseCase.execute(it) }
            .onEach {
                when (it) {
                    is Resource.Loading -> discoverScreenState.tryEmit(ScreenState.Loading)
                    is Resource.Error -> discoverScreenState.tryEmit(ScreenState.Error(it.throwable))
                    is Resource.Success -> discoverScreenState.tryEmit(ScreenState.Success)
                }
            }
            .filterIsInstance<Resource.Success<StoreGroupingData>>()
            .flatMapLatest { getDirectoryPagedList(it.data) }
    ).flattenMerge()
        .cachedIn(viewModelScope)

    private fun getDirectoryPagedList(storeData: StoreGroupingData): Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 3,
                enablePlaceholders = false,
                maxSize = 100,
                prefetchDistance = 2
            )
        ) {
            StoreDataPagingSource(storeData, getStoreItemsUseCase)
        }.flow
}


