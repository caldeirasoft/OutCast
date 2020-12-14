package com.caldeirasoft.outcast.ui.screen.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.data.repository.StoreRepositoryImpl
import com.caldeirasoft.outcast.data.util.StoreChartsPagingSource
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class StoreViewModel(
    private val fetchStoreDirectoryUseCase: FetchStoreDirectoryUseCase,
    private val fetchStoreTopChartsUseCase: FetchStoreTopChartsUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
) : ViewModel() {
    private val storeTabs = MutableStateFlow(StoreTab.values().asList())
    private val selectedStoreTab = MutableStateFlow(StoreTab.DISCOVER)
    private val chartList = MutableStateFlow(Chart.values().asList())
    private val selectedChart = MutableStateFlow(Chart.PODCASTS)
    private val storeFront = fetchStoreFrontUseCase.getStoreFront()
    private val screenState = MutableStateFlow<ScreenState>(ScreenState.Success)

    private val storeData: StateFlow<StoreGroupingData?> =
        MutableStateFlow(null)

    val genres: MutableStateFlow<List<StoreGenre>> =
        MutableStateFlow(emptyList())

    val discover: Flow<PagingData<StoreItem>> =
        getStoreDataPagedList()
            .cachedIn(viewModelScope)

    val topPodcastsChart: Flow<PagingData<StoreItem>> =
        storeFront
            .flatMapLatest { getTopPodcastsChartPagedList(storeFront = it) }
            .cachedIn(viewModelScope)

    val topEpisodesChart: Flow<PagingData<StoreItem>> =
        storeFront
            .flatMapLatest { getTopEpisodesChartPagedList(storeFront = it) }
            .cachedIn(viewModelScope)

    val state: StateFlow<StoreViewState> =
        combine(storeTabs, selectedStoreTab, chartList, selectedChart, screenState)
        { tabs, selectedTab, chartList, selectedChart, screenState ->
            StoreViewState(tabs, selectedTab, chartList, selectedChart, screenState)
        }.distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Lazily, StoreViewState())

    fun onStoreTabSelected(storeTab: StoreTab) {
        selectedStoreTab.value = storeTab
    }

    fun onChartSelected(chart: Chart) {
        selectedChart.value = chart
    }

    /*
    fun onGenreDiscoverSelected(storeGenre: StoreGenre, level: Int) {
        //if not selected
        val queue = selectedGenres.value.toMutableList()
        if (!queue.contains(storeGenre)) {
            for (item in queue.drop(level))
                queue.remove(item)
            queue.add(storeGenre)
            selectedGenres.tryEmit(queue)
        } else {
            for (item in queue.drop(level))
                queue.remove(item)
            selectedGenres.tryEmit(queue)
        }
    }*/

    private fun getStoreDataPagedList(): Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 3,
                enablePlaceholders = false,
                maxSize = 100,
                prefetchDistance = 2
            )
        ) {
            object : StoreDataPagingSource(scope = viewModelScope) {
                override fun fetchStoreData(): Flow<StorePage> =
                    storeFront
                        .flatMapLatest { fetchStoreDirectoryUseCase.execute(it) }
                        .onEach {
                            when (it) {
                                is Resource.Loading -> screenState.tryEmit(ScreenState.Loading)
                                is Resource.Error -> screenState.tryEmit(ScreenState.Error(it.throwable))
                                is Resource.Success -> screenState.tryEmit(ScreenState.Success)
                            }
                        }
                        .filterIsInstance<Resource.Success<StoreGroupingData>>()
                        .map { it.data }
                        .onEach { genres.emit(it.genres.orEmpty()) }
            }
        }.flow


    private fun getTopPodcastsChartPagedList(storeFront: String)
            : Flow<PagingData<StoreItem>> =
        getTopChartPagedList {
            object : StoreChartsPagingSource.Podcasts(scope = viewModelScope) {
                override fun fetchStoreTopCharts() = fetchStoreTopCharts(storeFront)

            }
        }

    private fun getTopEpisodesChartPagedList(storeFront: String)
            : Flow<PagingData<StoreItem>> =
        getTopChartPagedList {
            object : StoreChartsPagingSource.Episodes(scope = viewModelScope) {
                override fun fetchStoreTopCharts() = fetchStoreTopCharts(storeFront)

            }
        }


    private fun getTopChartPagedList(
        pagingSourceFactory: () -> StoreChartsPagingSource
    ): Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                maxSize = 200,
                prefetchDistance = 3
            )
        ) {
            pagingSourceFactory()
        }.flow


    private fun fetchStoreTopCharts(storeFront: String): Flow<StoreTopCharts> =
        fetchStoreTopChartsUseCase.execute(
            StoreRepositoryImpl.TOP_CHARTS_URL,
            storeFront = storeFront
        )
            .filterIsInstance<Resource.Success<StoreTopCharts>>()
            .map { it.data }
}


