package com.caldeirasoft.outcast.ui.screen.store.storeroom

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDataUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.screen.store.base.StoreRoomBaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class StoreRoomViewModel(
    private val room: StoreRoom,
    private val fetchStoreDataUseCase: FetchStoreDataUseCase,
    private val getStoreItemsUseCase: GetStoreItemsUseCase,
) : StoreRoomBaseViewModel<StorePage>() {

    val state: StateFlow<State<StorePage>> = storeDataState

    override fun getPagingConfig() = PagingConfig(
        pageSize = 10,
        enablePlaceholders = false,
        maxSize = 100,
        prefetchDistance = 3
    )

    init {
        run {
            if (room.url.isEmpty()) flowOf(Resource.Success(room.page))
            else fetchStoreDataUseCase.executeAsync(room.url, room.storeFront)
        }
            .onEach { storeResourceData.emit(it) }
            .launchIn(viewModelScope)
    }
}