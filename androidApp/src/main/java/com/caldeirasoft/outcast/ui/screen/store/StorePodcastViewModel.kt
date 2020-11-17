package com.caldeirasoft.outcast.ui.screen.store

import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.StorePodcast
import com.caldeirasoft.outcast.domain.usecase.FetchStoreItemsUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStorePodcastDataUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class StorePodcastViewModel(
    private val fetchStorePodcastDataUseCase: FetchStorePodcastDataUseCase,
    val fetchStoreItemsUseCase: FetchStoreItemsUseCase,
) : StoreBaseViewModel() {

    private val storeDataPodcast
            = MutableStateFlow<Resource<StorePodcast>>(Resource.Loading(null))

    val storeDataPodcastState
        get() = storeDataPodcast

    fun fetchPodcast(url: String) {
        viewModelScope.launch {
            fetchStorePodcastDataUseCase
                .invoke(FetchStorePodcastDataUseCase.Params(url = url, storeFront = storeFront))
                .onEach { storeDataPodcast.emit(it) }
        }
    }
}