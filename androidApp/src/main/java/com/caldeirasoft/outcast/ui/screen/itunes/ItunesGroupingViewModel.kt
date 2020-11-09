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

class ItunesGroupingViewModel(
    private val fetchItunesGroupingDataUseCase: FetchItunesGroupingDataUseCase,
    val fetchItunesListStoreItemsUseCase: FetchItunesListStoreItemsUseCase,
) : ItunesBaseViewModel(fetchItunesListStoreItemsUseCase = fetchItunesListStoreItemsUseCase) {

    private val storeDataGrouping
            = MutableStateFlow<Resource<StoreDataGrouping>>(Resource.Loading(null))

    val storeDataGroupingState
        get() = storeDataGrouping

    fun fetchGrouping(url: String) {
        viewModelScope.launch {
            fetchItunesGroupingDataUseCase
                .invoke(FetchItunesGroupingDataUseCase.Params(url = url, storeFront = storeFront))
                .onEach { storeDataGrouping.emit(it) }
        }
    }
}