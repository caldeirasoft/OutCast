package com.caldeirasoft.outcast.ui.screen.store.genre

import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingData
import com.caldeirasoft.outcast.domain.usecase.FetchStoreGroupingUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreCollectionsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
@ExperimentalCoroutinesApi
class StoreGenreViewModel(genreId: Int) : StoreCollectionsViewModel<StoreGroupingData>(), KoinComponent {
    private val fetchStoreGroupingUseCase: FetchStoreGroupingUseCase by inject()
    // selected tab
    private val selectedChartTab = MutableStateFlow(StoreItemType.PODCAST)
    // state
    val state = MutableStateFlow(State())

    init {
        storeFront
            .flatMapConcat {
                fetchStoreGroupingUseCase
                    .executeAsync(genreId = genreId, storeFront = it)
            }
            .onEach { storeResourceData.emit(it) }
            .launchIn(viewModelScope)

        combine(storeResourceData, selectedChartTab) { storeResourceData, selectedChartTab ->
            State(storeResourceData, selectedChartTab)
        }
            .onEach { state.emit(it) }
            .launchIn(viewModelScope)
    }

    fun onChartTabSelected(tab: StoreItemType) {
        selectedChartTab.tryEmit(tab)
    }

    data class State(
        val storeResourceData: Resource = Resource.Loading,
        val selectedChartTab: StoreItemType = StoreItemType.PODCAST,
    )
}