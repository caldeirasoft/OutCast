package com.caldeirasoft.outcast.ui.screen.store.topchartitem

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
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreTopChartsUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.screen.store.discover.DiscoverViewState
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
abstract class TopChartsItemViewModel(
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    private val fetchStoreTopChartsUseCase: FetchStoreTopChartsUseCase,
    private val getStoreItemsUseCase: GetStoreItemsUseCase,
) : ViewModel() {

    private val storeFront = fetchStoreFrontUseCase.getStoreFront()
    private val screenState = MutableStateFlow<ScreenState>(ScreenState.Idle)

    val state: StateFlow<TopChartsViewState> =
        combine(screenState, storeFront)
        { screenState, storeFront -> TopChartsViewState(screenState) }
            .stateIn(viewModelScope, SharingStarted.Lazily, TopChartsViewState())

    val topCharts: Flow<PagingData<StoreItem>> = flowOf(
        storeFront
            .flatMapLatest { fetchStoreTopChartsUseCase.execute(it) }
            .onEach {
                when (it) {
                    is Resource.Loading -> screenState.tryEmit(ScreenState.Loading)
                    is Resource.Error -> screenState.tryEmit(ScreenState.Error(it.throwable))
                    is Resource.Success -> screenState.tryEmit(ScreenState.Success)
                }
            }
            .filterIsInstance<Resource.Success<StoreTopCharts>>()
            .map { storeData ->
                storeData.data.let {
                    StoreRoom(
                        id = 0L,
                        label = it.label,
                        storeFront = it.storeFront,
                        storeIds = getStoreTopChartsIds(it)
                    )
                }
            }
            .flatMapLatest { room -> getTopChartsPagedList(room) }
    ).flattenMerge()
        .cachedIn(viewModelScope)

    private fun getTopChartsPagedList(storeData: StoreRoom): Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                maxSize = 200,
                prefetchDistance = 3
            )
        ) {
            StoreDataPagingSource(storeData, getStoreItemsUseCase)
        }.flow

    abstract fun getStoreTopChartsIds(storeData: StoreTopCharts): List<Long>
}