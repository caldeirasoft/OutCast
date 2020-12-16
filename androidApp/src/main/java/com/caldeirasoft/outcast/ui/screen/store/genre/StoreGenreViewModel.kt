package com.caldeirasoft.outcast.ui.screen.store.genre

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.store.StoreChart
import com.caldeirasoft.outcast.domain.models.store.StoreGenre
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingData
import com.caldeirasoft.outcast.domain.models.store.StoreTopCharts
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDataUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreTopChartsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.screen.store.base.StoreChartsBaseViewModel
import com.caldeirasoft.outcast.ui.screen.store.base.StoreRoomBaseViewModel
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
@ExperimentalCoroutinesApi
class StoreGenreViewModel(
    private val storeGenre: StoreGenre,
) : StoreRoomBaseViewModel<StoreGroupingData>(), KoinComponent
{
    private val fetchStoreDataUseCase: FetchStoreDataUseCase by inject()
    private val fetchStoreTopChartsUseCase: FetchStoreTopChartsUseCase by inject()
    private val topChartScreenState = MutableStateFlow<ScreenState>(ScreenState.Idle)

    private val topChartData: StateFlow<StoreTopCharts?> =
            fetchStoreTopChartsUseCase.execute(storeGenre.topChartsUrl, storeGenre.storeFront)
                .onEach {
                    when (it) {
                        is Resource.Loading -> topChartScreenState.tryEmit(ScreenState.Loading)
                        is Resource.Error -> topChartScreenState.tryEmit(ScreenState.Error(it.throwable))
                        is Resource.Success -> topChartScreenState.tryEmit(ScreenState.Success)
                    }
                }
                .filterIsInstance<Resource.Success<StoreTopCharts>>()
                .map { it.data }
                .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    var state: StateFlow<State>

    override fun getPagingConfig() = PagingConfig(
        pageSize = 4,
        enablePlaceholders = false,
        maxSize = 100,
        prefetchDistance = 2
    )

    init {
        state = combine(storeDataState, topChartScreenState, topChartData)
        { storeViewState, topChartScreenState, topChartData ->
            State(
                storeViewState.screenState,
                storeViewState.storeData,
                topChartScreenState,
                topChartData)
        }.distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.Eagerly, State())

        storeGenre
            .let { fetchStoreDataUseCase.executeAsync(it.url, it.storeFront) }
            .onEach { it -> storeResourceData.emit(it) }
            .launchIn(viewModelScope)
    }

    data class State(
        val storeState: ScreenState = ScreenState.Idle,
        val storeData: StoreGroupingData? = null,
        val chartState: ScreenState = ScreenState.Idle,
        val chartData: StoreTopCharts? = null,
    )
}


