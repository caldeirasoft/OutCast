package com.caldeirasoft.outcast.ui.screen.store.storeroom

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.interfaces.StoreFeaturedPage
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.usecase.FetchStoreRoomPagingDataUseCase
import com.caldeirasoft.outcast.domain.util.tryCast
import com.caldeirasoft.outcast.ui.util.ListState
import com.caldeirasoft.outcast.ui.util.ScrollViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
@ExperimentalCoroutinesApi
class StoreRoomViewModel(
    initialState: StoreRoomViewState,
) : MavericksViewModel<StoreRoomViewState>(initialState), KoinComponent, ScrollViewModel {

    private val fetchStoreRoomPagingDataUseCase: FetchStoreRoomPagingDataUseCase by inject()
    override var scrollState: ListState = ListState()

    // paged list
    val discover: Flow<PagingData<StoreItem>> =
        getRoomPagedList()
            .cachedIn(viewModelScope)

    // get paged list
    @OptIn(FlowPreview::class)
    private fun getRoomPagedList(): Flow<PagingData<StoreItem>> =
        stateFlow
            .map { it.room }
            .distinctUntilChanged()
            .flatMapConcat { room ->
                fetchStoreRoomPagingDataUseCase.executeAsync(
                    scope = viewModelScope,
                    storeRoom = room,
                    dataLoadedCallback = { page ->
                        page.tryCast<StoreFeaturedPage> {
                            setState { copy(storePage = this@tryCast) }
                        }
                    })
            }
}