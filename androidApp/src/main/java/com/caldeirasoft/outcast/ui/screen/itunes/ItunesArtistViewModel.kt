package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.StoreDataArtist
import com.caldeirasoft.outcast.domain.usecase.FetchItunesArtistDataUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchItunesListStoreItemsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ItunesArtistViewModel(
    private val fetchItunesArtistDataUseCase: FetchItunesArtistDataUseCase,
    val fetchItunesListStoreItemsUseCase: FetchItunesListStoreItemsUseCase,
) : ItunesBaseViewModel(fetchItunesListStoreItemsUseCase = fetchItunesListStoreItemsUseCase) {

    private val storeArtistRoom
            = MutableStateFlow<Resource<StoreDataArtist>>(Resource.Loading(null))

    val storeArtistRoomState
        get() = storeArtistRoom

    fun fetchArtist(url: String) {
        viewModelScope.launch {
            fetchItunesArtistDataUseCase
                .invoke(FetchItunesArtistDataUseCase.Params(url = url, storeFront = storeFront))
                .onEach { storeArtistRoom.emit(it) }
        }
    }
}