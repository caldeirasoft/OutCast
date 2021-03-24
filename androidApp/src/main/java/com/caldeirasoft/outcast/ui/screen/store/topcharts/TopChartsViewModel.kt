package com.caldeirasoft.outcast.ui.screen.store.topcharts

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreTopCharts
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStoreTopChartsPagingDataUseCase
import com.caldeirasoft.outcast.domain.util.tryCast
import com.caldeirasoft.outcast.ui.screen.store.base.FollowViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
@FlowPreview
@ExperimentalCoroutinesApi
class TopChartsViewModel(
    initialState: TopChartsViewState,
) : FollowViewModel<TopChartsViewState>(initialState), KoinComponent {
    private val loadStoreTopChartsPagingDataUseCase: LoadStoreTopChartsPagingDataUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()

    init {
        followingStatus.setOnEach { copy(followingStatus = it) }
    }

    // paged list
    val topCharts: Flow<PagingData<StoreItem>> =
        stateFlow
            .map { Pair(it.selectedGenre, it.selectedChartTab) }
            .distinctUntilChanged()
            .combine(fetchStoreFrontUseCase.getStoreFront()) { statePair, storeFront ->
                loadStoreTopChartsPagingDataUseCase.execute(
                    scope = viewModelScope,
                    genreId = statePair.first, // genre
                    storeFront = storeFront,
                    storeItemType = statePair.second, // item type
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
            .cachedIn(viewModelScope)

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


