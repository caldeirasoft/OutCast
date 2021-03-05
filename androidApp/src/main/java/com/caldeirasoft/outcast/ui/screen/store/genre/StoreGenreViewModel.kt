package com.caldeirasoft.outcast.ui.screen.store.genre

import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.usecase.FetchStoreGroupingPagingDataUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
class StoreGenreViewModel(
    initialState: StoreGenreViewState,
) : MavericksViewModel<StoreGenreViewState>(initialState), KoinComponent {

    private val fetchStoreGroupingPagingDataUseCase: FetchStoreGroupingPagingDataUseCase by inject()

    init {
        viewModelScope.launch {
            getGenrePagedList()
        }
    }

    // get paged list
    @OptIn(FlowPreview::class)
    private fun getGenrePagedList() {
        withState { state ->
            fetchStoreGroupingPagingDataUseCase.executeAsync(
                scope = viewModelScope,
                genre = state.genreId,
                storeFront = state.storeFront,
                dataLoadedCallback = {})
                .cachedIn(viewModelScope)
                .setOnEach {
                    copy(discover = it)
                }
        }
    }
}