package com.caldeirasoft.outcast.ui.screen.store.storeroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.domain.interfaces.StoreFeaturedPage
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.domain.usecase.FetchStoreRoomPagingDataUseCase
import com.caldeirasoft.outcast.domain.util.tryCast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class StoreRoomViewModel(
    private val room: StoreRoom,
    private val fetchStoreRoomPagingDataUseCase: FetchStoreRoomPagingDataUseCase
) : ViewModel() {

    // store data
    protected val storeData = MutableStateFlow<StoreFeaturedPage>(room.getPage())

    // state
    val state: StateFlow<State> =
        storeData.map { State(storePage = it)}
            .stateIn(viewModelScope, SharingStarted.Eagerly, State(room.getPage()))

    // paged list
    val discover: Flow<PagingData<StoreItem>> =
        getStoreDataPagedList()
            .cachedIn(viewModelScope)

    private fun getStoreDataPagedList(): Flow<PagingData<StoreItem>> =
        fetchStoreRoomPagingDataUseCase.executeAsync(
            storeRoom = room,
            dataLoadedCallback = {
                it.tryCast<StoreFeaturedPage> {
                    storeData.tryEmit(this)
                }
            }
        )

    data class State(
        val storePage: StoreFeaturedPage
    )

    companion object {
        private const val ROOM_SAVED_STATE_KEY = "ROOM_SAVED_STATE_KEY"
    }
}