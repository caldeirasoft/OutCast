package com.caldeirasoft.outcast.ui.screen.store

import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StoreDirectoryViewModel(
    private val fetchStoreDirectoryUseCase: FetchStoreDirectoryUseCase,
    val fetchStoreItemsUseCase: FetchStoreItemsUseCase,
) : StoreBaseViewModel(fetchStoreItemsUseCase = fetchStoreItemsUseCase) {

    private val storeDataGrouping
            = MutableStateFlow<Resource<StoreDataGrouping>>(Resource.Loading())

    val storeDataGroupingState: StateFlow<Resource<StoreDataGrouping>>
            = storeDataGrouping

    init {
        viewModelScope.launch {
            fetchStoreDirectoryUseCase
                .invoke(FetchStoreDirectoryUseCase.Params(storeFront = storeFront))
                .collect {
                    storeDataGrouping.emit(it)
                }
        }
    }
    val testMessage: String = "Test Message"
}