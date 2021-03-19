package com.caldeirasoft.outcast.ui.screen.store.topcharts

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreTopCharts
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStoreTopChartsPagingDataUseCase
import com.caldeirasoft.outcast.domain.util.tryCast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flattenMerge
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

    // paged list
    val topCharts: Flow<PagingData<StoreItem>> =
        getTopChartsPagedList()
            .cachedIn(viewModelScope)

    // get paged list
    @OptIn(FlowPreview::class)
    private fun getTopChartsPagedList(): Flow<PagingData<StoreItem>> =
        stateFlow
            .combine(fetchStoreFrontUseCase.getStoreFront()) { state, storeFront ->
                loadStoreTopChartsPagingDataUseCase.execute(
                    scope = viewModelScope,
                    genreId = state.selectedGenre,
                    storeFront = storeFront,
                    storeItemType = state.selectedChartTab,
                    dataLoadedCallback = { page ->
                        page.tryCast<StoreTopCharts> {
                            val topCharts = this
                            setState {
                                copy(categories = topCharts.categories)
                            }
                        }
                    })
            }
            .flattenMerge()


    fun onTabSelected(tab: StoreItemType) {
        setState {
            copy(selectedChartTab = tab)
        }
    }

    fun onGenreSelected(genreId: Int?) {
        setState {
            copy(selectedGenre = genreId)
        }
    }
}


