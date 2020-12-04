package com.caldeirasoft.outcast.ui.screen.storeroom

import android.util.Log
import androidx.compose.runtime.emit
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
import com.caldeirasoft.outcast.domain.models.StoreRoom
import com.caldeirasoft.outcast.domain.models.StoreTopCharts
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDataUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopChartsViewState
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.flow.*

class StoreCollectionViewModel(
    private val fetchStoreDataUseCase: FetchStoreDataUseCase,
    private val getStoreItemsUseCase: GetStoreItemsUseCase,
    private val room: StoreRoom
) : ViewModel()
{
    private val screenState = MutableStateFlow<ScreenState>(ScreenState.Idle)

    private val storeResourceData: Flow<Resource<StoreData>> =
        run {
            if (room.url.isEmpty()) flowOf(Resource.Success(room))
            else fetchStoreDataUseCase.execute(room.url, room.storeFront)
        }.onEach {
                when (it) {
                    is Resource.Loading -> screenState.tryEmit(ScreenState.Loading)
                    is Resource.Error -> screenState.tryEmit(ScreenState.Error(it.throwable))
                    is Resource.Success -> screenState.tryEmit(ScreenState.Success)
                }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, Resource.Loading())

    private val storeData: Flow<StoreData> =
        storeResourceData
            .filterIsInstance<Resource.Success<StoreData>>()
            .map { it.data }

    val state: StateFlow<StoreCollectionViewState> =
        combine(screenState, storeData)
        { screenState, storeData -> StoreCollectionViewState(screenState, storeData) }
            .stateIn(viewModelScope, SharingStarted.Lazily, StoreCollectionViewState())

    val pagedList: Flow<PagingData<StoreItem>> =
        storeData
            .flatMapLatest { data -> getPagedList(data) }
            .cachedIn(viewModelScope)


    private fun getPagedList(storeData: StoreData): Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                maxSize = 100,
                prefetchDistance = 3
            )
        ) {
            StoreDataPagingSource(storeData, getStoreItemsUseCase)
        }.flow
}