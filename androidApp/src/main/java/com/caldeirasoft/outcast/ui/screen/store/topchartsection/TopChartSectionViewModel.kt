package com.caldeirasoft.outcast.ui.screen.store.topchartsection

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
@ExperimentalCoroutinesApi
abstract class TopChartSectionViewModel(
    initialState: TopChartSectionState,
    val storeItemType: StoreItemType,
) : FollowViewModel<TopChartSectionState>(initialState), KoinComponent {
    private val loadStoreTopChartsPagingDataUseCase: LoadStoreTopChartsPagingDataUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()

    init {
        followingStatus.setOnEach { copy(followingStatus = it) }
    }

    // paged list
    @OptIn(FlowPreview::class)
    val topCharts: Flow<PagingData<StoreItem>> =
        stateFlow
            .map { it.selectedGenre }
            .distinctUntilChanged()
            .combine(fetchStoreFrontUseCase.getStoreFront()) { selectedGenre, storeFront ->
                loadStoreTopChartsPagingDataUseCase.execute(
                    scope = viewModelScope,
                    genreId = selectedGenre, // genre
                    storeFront = storeFront,
                    storeItemType = storeItemType, // item type
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

    fun onGenreSelected(genreId: Int?) {
        setState {
            copy(selectedGenre = genreId)
        }
    }
}


