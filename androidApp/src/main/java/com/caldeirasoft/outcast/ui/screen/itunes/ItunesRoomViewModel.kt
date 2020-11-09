package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ItunesRoomViewModel(
    private val fetchItunesRoomDataUseCase: FetchItunesRoomDataUseCase,
    val fetchItunesListStoreItemsUseCase: FetchItunesListStoreItemsUseCase,
) : ItunesBaseViewModel(fetchItunesListStoreItemsUseCase = fetchItunesListStoreItemsUseCase) {

    private val storeDataRoom
            = MutableStateFlow<Resource<StoreDataRoom>>(Resource.Loading(null))

    val storeDataRoomState
        get() = storeDataRoom

    fun fetchRoom(url: String) {
        viewModelScope.launch {
            fetchItunesRoomDataUseCase
                .invoke(FetchItunesRoomDataUseCase.Params(url = url, storeFront = storeFront))
                .onEach { storeDataRoom.emit(it) }
        }
    }
}