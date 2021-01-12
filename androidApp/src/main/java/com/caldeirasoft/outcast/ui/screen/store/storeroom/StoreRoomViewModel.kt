package com.caldeirasoft.outcast.ui.screen.store.storeroom

import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDataUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreCollectionsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@ExperimentalCoroutinesApi
class StoreRoomViewModel(
    private val room: StoreRoom,
) : StoreCollectionsViewModel<StorePage>(), KoinComponent {
    private val fetchStoreDataUseCase: FetchStoreDataUseCase by inject()

    // state
    val state: StateFlow<State> =
        storeResourceData.map { State(storeResourceData = it)}
            .stateIn(viewModelScope, SharingStarted.Eagerly, State())

    init {
        run {
            if (room.url.isEmpty()) flowOf(Resource.Success(room.page))
            else fetchStoreDataUseCase.executeAsync(room.url, room.storeFront)
        }
            .onEach { storeResourceData.emit(it) }
            .launchIn(viewModelScope)
    }

    data class State(
        val storeResourceData: Resource = Resource.Loading,
    )
}