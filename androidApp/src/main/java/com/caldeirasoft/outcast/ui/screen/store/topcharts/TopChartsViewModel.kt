package com.caldeirasoft.outcast.ui.screen.store.topcharts

import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.models.store.StoreTopCharts
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStoreTopChartsPagingDataUseCase
import com.caldeirasoft.outcast.domain.util.tryCast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
@FlowPreview
@ExperimentalCoroutinesApi
class TopChartsViewModel(
    initialState: TopChartsViewState,
) : MavericksViewModel<TopChartsViewState>(initialState), KoinComponent {
    private val loadStoreTopChartsPagingDataUseCase: LoadStoreTopChartsPagingDataUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()

    init {
        viewModelScope.launch {
            getTopChartsPagedList()
        }
    }

    // get paged list
    @OptIn(FlowPreview::class)
    private suspend fun getTopChartsPagedList() {
        val store = fetchStoreFrontUseCase.getStoreFront().first()
        withState { state ->
            loadStoreTopChartsPagingDataUseCase.execute(
                scope = viewModelScope,
                genreId = state.selectedGenre,
                storeFront = store,
                storeItemType = state.selectedChartTab,
                dataLoadedCallback = { page ->
                    page.tryCast<StoreTopCharts> {
                        val topCharts = this
                        setState {
                            copy(categories = topCharts.categories)
                        }
                    }
                })
                .cachedIn(viewModelScope)
                .setOnEach { copy(discover = it) }
        }
    }

    fun onTabSelected(tab: StoreItemType) {
        viewModelScope.launch {
            setState {
                copy(selectedChartTab = tab)
            }
            getTopChartsPagedList()
        }
    }

    fun onGenreSelected(genreId: Int?) {
        viewModelScope.launch {
            setState {
                copy(selectedGenre = genreId)
            }
            getTopChartsPagedList()
        }
    }
}


