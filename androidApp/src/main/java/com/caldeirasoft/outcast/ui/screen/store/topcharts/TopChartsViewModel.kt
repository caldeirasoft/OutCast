package com.caldeirasoft.outcast.ui.screen.store.topcharts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.data.util.StoreChartsPagingSource
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreTopChartsIdsUseCase
import com.caldeirasoft.outcast.ui.screen.store.base.StoreChartTab
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@ExperimentalCoroutinesApi
class TopChartsViewModel(genreId: Int?)
    : ViewModel(), KoinComponent {
    private val fetchStoreTopChartsIdsUseCase: FetchStoreTopChartsIdsUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()

    // storefront
    private val storeFront = fetchStoreFrontUseCase.getStoreFront()
    // selected tab
    private val selectedTab = MutableStateFlow(StoreItemType.PODCAST)

    // topPodcastsCharts
    val topPodcastsCharts: Flow<PagingData<StoreItem>> =
        storeFront
            .flatMapLatest { getTopChartPagedList(genreId = genreId, type = StoreItemType.PODCAST, storeFront = it) }
            .cachedIn(viewModelScope)

    // topEpisodesCharts
    val topEpisodesCharts: Flow<PagingData<StoreItem>> =
        storeFront
            .flatMapLatest { getTopChartPagedList(genreId = genreId, type = StoreItemType.EPISODE, storeFront = it) }
            .cachedIn(viewModelScope)

    private fun getTopChartPagedList(genreId: Int?, type: StoreItemType, storeFront: String): Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                maxSize = 200,
                prefetchDistance = 5
            )
        ) {
            StoreChartsPagingSource(
                storeFront = storeFront,
                scope = viewModelScope) {
                fetchStoreTopChartsIdsUseCase.execute(storeGenre = genreId, storeItemType = type, storeFront = storeFront)
            }
        }.flow

    fun onTabSelected(tab: StoreItemType) {
        selectedTab.tryEmit(tab)
    }

    data class State(
        val screenState: ScreenState = ScreenState.Idle,
        val selectedChartTab: StoreChartTab = StoreChartTab.Podcasts,
        val storeFront: String? = null,
    )
}


