package com.caldeirasoft.outcast.ui.screen.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.data.util.TopChartsPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreCollectionPodcastsEpisodes
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.StoreDirectory
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDirectoryUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class StoreViewModel(
    private val fetchStoreDirectoryUseCase: FetchStoreDirectoryUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    private val getStoreItemsUseCase: GetStoreItemsUseCase,
) : ViewModel() {
    private val selectedStoreTab = MutableStateFlow(StoreTab.DISCOVER)
    private val storeTabs = MutableStateFlow(StoreTab.values().asList())
    private val selectedTopChartsTab = MutableStateFlow(TopChartsTab.PODCASTS)
    private val topChartsTabs = MutableStateFlow(TopChartsTab.values().asList())
    private val storeFront = fetchStoreFrontUseCase.getStoreFront()
    private val screenState = MutableStateFlow<ScreenState>(ScreenState.Idle)

    private val storeDirectoryData: StateFlow<StoreDirectory?> = flowOf(
        storeFront
            .flatMapLatest { fetchStoreDirectoryUseCase.execute(it) }
            .onEach {
                when (it) {
                    is Resource.Loading -> screenState.tryEmit(ScreenState.Loading)
                    is Resource.Error -> screenState.tryEmit(ScreenState.Error(it.throwable))
                    is Resource.Success -> screenState.tryEmit(ScreenState.Success)
                }
            }
            .filterIsInstance<Resource.Success<StoreDirectory>>()
            .map { it.data }
    ).flattenMerge()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val discover: Flow<PagingData<StoreItem>> =
        storeDirectoryData
            .filterNotNull()
            .flatMapLatest { getDirectoryPagedList(it) }
            .cachedIn(viewModelScope)

    val topChartsPodcasts: Flow<PagingData<StoreItem>> =
        storeDirectoryData
            .filterNotNull()
            .flatMapLatest { getTopChartsPagedList(it.topPodcasts, it) }
            .cachedIn(viewModelScope)

    val topChartsEpisodes: Flow<PagingData<StoreItem>> =
        storeDirectoryData
            .filterNotNull()
            .flatMapLatest { getTopChartsPagedList(it.topEpisodes, it) }
            .cachedIn(viewModelScope)

    val state: StateFlow<StoreViewState> =
        combine(storeTabs, selectedStoreTab, topChartsTabs, selectedTopChartsTab, screenState)
        { tabs, selectedTab, topChartsTabs, selectedTopChartsTab, screenState ->
            StoreViewState(tabs, selectedTab, topChartsTabs, selectedTopChartsTab, screenState)
        }.stateIn(viewModelScope, SharingStarted.Lazily, StoreViewState())

    fun onStoreTabSelected(storeTab: StoreTab) {
        selectedStoreTab.value = storeTab
    }

    fun onTopChartsTabSelected(topChartsTab: TopChartsTab) {
        selectedTopChartsTab.value = topChartsTab
    }

    private fun getDirectoryPagedList(storeData: StoreDirectory): Flow<PagingData<StoreItem>> =
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

    private fun getTopChartsPagedList(collection: StoreCollectionPodcastsEpisodes, storeDirectory: StoreDirectory): Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                maxSize = 200,
                prefetchDistance = 3
            )
        ) {
            TopChartsPagingSource(collection, storeDirectory, getStoreItemsUseCase)
        }.flow
}


