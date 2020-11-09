package com.caldeirasoft.outcast.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.StoreDataPodcast
import com.caldeirasoft.outcast.domain.usecase.FetchItunesListStoreItemsUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchItunesPodcastDataUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ItunesPodcastViewModel(
    private val fetchItunesPodcastDataUseCase: FetchItunesPodcastDataUseCase,
    val fetchItunesListStoreItemsUseCase: FetchItunesListStoreItemsUseCase,
) : ItunesBaseViewModel(fetchItunesListStoreItemsUseCase = fetchItunesListStoreItemsUseCase) {

    private val storeDataPodcast
            = MutableStateFlow<Resource<StoreDataPodcast>>(Resource.Loading(null))

    val storeDataPodcastState
        get() = storeDataPodcast

    fun fetchPodcast(url: String) {
        viewModelScope.launch {
            fetchItunesPodcastDataUseCase
                .invoke(FetchItunesPodcastDataUseCase.Params(url = url, storeFront = storeFront))
                .onEach { storeDataPodcast.emit(it) }
        }
    }
}