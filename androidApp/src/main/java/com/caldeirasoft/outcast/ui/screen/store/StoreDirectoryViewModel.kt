package com.caldeirasoft.outcast.ui.screen.store

import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class StoreDirectoryViewModel(
    private val fetchStoreDirectoryUseCase: FetchStoreDirectoryUseCase,
    val fetchStoreItemsUseCase: FetchStoreItemsUseCase,
) : StoreBaseViewModel(fetchStoreItemsUseCase = fetchStoreItemsUseCase) {

    private val storeDataGrouping
            = MutableStateFlow<Resource<StoreDataGrouping>>(Resource.Loading(null))

    val storeDataGroupingState
            = storeDataGrouping

    init {
        viewModelScope.launch {
            fetchStoreDirectoryUseCase
                .invoke(FetchStoreDirectoryUseCase.Params(storeFront = storeFront))
                .onEach { storeDataGrouping.emit(it) }
        }
    }
    val testMessage: String = "Test Message"
}