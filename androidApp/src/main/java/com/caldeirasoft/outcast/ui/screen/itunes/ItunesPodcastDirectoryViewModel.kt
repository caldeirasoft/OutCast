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

class ItunesPodcastDirectoryViewModel(
    private val fetchItunesPodcastDirectoryUseCase: FetchItunesPodcastDirectoryUseCase,
    val fetchItunesListStoreItemsUseCase: FetchItunesListStoreItemsUseCase,
) : ItunesBaseViewModel(fetchItunesListStoreItemsUseCase = fetchItunesListStoreItemsUseCase) {

    private val storeDataGrouping
            = MutableStateFlow<Resource<StoreDataGrouping>>(Resource.Loading(null))

    val storeDataGroupingState
            = storeDataGrouping

    init {
        viewModelScope.launch {
            fetchItunesPodcastDirectoryUseCase
                .invoke(FetchItunesPodcastDirectoryUseCase.Params(storeFront = storeFront))
                .onEach { storeDataGrouping.emit(it) }
        }
    }
    val testMessage: String = "Test Message"
}