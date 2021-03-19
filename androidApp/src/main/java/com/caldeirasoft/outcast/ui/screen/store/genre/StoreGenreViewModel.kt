package com.caldeirasoft.outcast.ui.screen.store.genre

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreGroupingPagingDataUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flattenMerge
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
class StoreGenreViewModel(
    initialState: StoreGenreViewState,
) : MavericksViewModel<StoreGenreViewState>(initialState), KoinComponent {

    private val fetchStoreGroupingPagingDataUseCase: FetchStoreGroupingPagingDataUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()

    // paged list
    val discover: Flow<PagingData<StoreItem>> =
        getGenrePagedList()
            .cachedIn(viewModelScope)

    // get paged list
    @OptIn(FlowPreview::class)
    private fun getGenrePagedList(): Flow<PagingData<StoreItem>> =
        stateFlow
            .distinctUntilChanged()
            .combine(fetchStoreFrontUseCase.getStoreFront()) { state, storeFront ->
                fetchStoreGroupingPagingDataUseCase.executeAsync(
                    scope = viewModelScope,
                    genre = state.genreId,
                    storeFront = storeFront,
                    dataLoadedCallback = { })
            }
            .flattenMerge()
}