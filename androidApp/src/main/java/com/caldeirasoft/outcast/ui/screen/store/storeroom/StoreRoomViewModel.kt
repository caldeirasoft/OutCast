package com.caldeirasoft.outcast.ui.screen.store.storeroom

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.interfaces.StoreFeaturedPage
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDataUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreGroupingUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreTopChartsIdsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreCollectionsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class StoreRoomViewModel(
    private val room: StoreRoom,
    val fetchStoreDataUseCase: FetchStoreDataUseCase,
    fetchStoreGroupingUseCase: FetchStoreGroupingUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    fetchStoreTopChartsIdsUseCase: FetchStoreTopChartsIdsUseCase
) : StoreCollectionsViewModel<StoreFeaturedPage>(
    fetchStoreFrontUseCase = fetchStoreFrontUseCase,
    fetchStoreGroupingUseCase = fetchStoreGroupingUseCase,
    fetchStoreTopChartsIdsUseCase = fetchStoreTopChartsIdsUseCase
) {
    // state
    val state: StateFlow<State> =
        storeData
            .filterNotNull()
            .map { State(storePage = it)}
            .stateIn(viewModelScope, SharingStarted.Eagerly, State(room.getPage()))

    override fun getStoreDataFlow(): Flow<Resource> =
        run {
            if (room.url.isEmpty()) flowOf(Resource.Success(room.getPage()))
            else fetchStoreDataUseCase.executeAsync(room.url, room.storeFront)
        }

    data class State(
        val storePage: StoreFeaturedPage
    )

    companion object {
        private const val ROOM_SAVED_STATE_KEY = "ROOM_SAVED_STATE_KEY"
    }
}