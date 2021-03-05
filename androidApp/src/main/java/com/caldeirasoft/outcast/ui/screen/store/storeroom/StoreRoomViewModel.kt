package com.caldeirasoft.outcast.ui.screen.store.storeroom

import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.interfaces.StoreFeaturedPage
import com.caldeirasoft.outcast.domain.usecase.FetchStoreRoomPagingDataUseCase
import com.caldeirasoft.outcast.domain.util.tryCast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
@ExperimentalCoroutinesApi
class StoreRoomViewModel(
    initialState: StoreRoomViewState,
) : MavericksViewModel<StoreRoomViewState>(initialState), KoinComponent {

    private val fetchStoreRoomPagingDataUseCase: FetchStoreRoomPagingDataUseCase by inject()

    init {
        viewModelScope.launch {
            getRoomPagedList()
        }
    }

    // get paged list
    @OptIn(FlowPreview::class)
    private fun getRoomPagedList() {
        withState { state ->
            fetchStoreRoomPagingDataUseCase.executeAsync(
                scope = viewModelScope,
                storeRoom = state.room,
                dataLoadedCallback = { page ->
                    page.tryCast<StoreFeaturedPage> {
                        setState { copy(storePage = this@tryCast) }
                    }
                })
                .cachedIn(viewModelScope)
                .setOnEach { copy(discover = it) }
        }
    }
}