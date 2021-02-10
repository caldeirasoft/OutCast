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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@FlowPreview
@OptIn(KoinApiExtension::class)
@ExperimentalCoroutinesApi
class TopChartsViewModel(storeItemType: StoreItemType) : ViewModel(), KoinComponent {
    private val fetchStoreTopChartsIdsUseCase: FetchStoreTopChartsIdsUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()

    // storefront
    private val storeFront = fetchStoreFrontUseCase.getStoreFront()
    // selected tab
    private val selectedTab = MutableStateFlow(storeItemType)
    // selected tab
    private val selectedGenre = MutableStateFlow<Int?>(null)
    // state
    val state = MutableStateFlow(State())

    // topEpisodesCharts
    val topCharts: Flow<PagingData<StoreItem>> =
        combine(storeFront, selectedTab, selectedGenre) {
                storeFront, selectedTab, selectedGenre ->
            getTopChartPagedList(genreId = selectedGenre, type = selectedTab, storeFront = storeFront)
        }
            .flattenMerge()
            .cachedIn(viewModelScope)

    init {
        combine(selectedTab, selectedGenre)
        { selectedTab, selectedGenre ->
            State(selectedTab, selectedGenre)
        }
            .onEach { state.emit(it) }
            .launchIn(viewModelScope)
    }


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

    fun onGenreSelected(genreId: Int?) {
        selectedGenre.tryEmit(genreId)
    }

    data class State(
        val selectedChartTab: StoreItemType = StoreItemType.PODCAST,
        val selectedGenre: Int? = null
    )
}


