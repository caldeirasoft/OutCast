package com.caldeirasoft.outcast.ui.screen.store

import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class StoreDataViewModel(
    private val fetchStoreDataUseCase: FetchStoreDataUseCase,
    val fetchStoreItemsUseCase: FetchStoreItemsUseCase,
) : StoreBaseViewModel(fetchStoreItemsUseCase = fetchStoreItemsUseCase) {

    private val storeData
            = MutableStateFlow<Resource<StoreData>>(Resource.Loading())

    val storeDataState
        get() = storeData

    fun fetchGrouping(url: String) {
        viewModelScope.launch {
            fetchStoreDataUseCase
                .invoke(FetchStoreDataUseCase.Params(url = url, storeFront = storeFront))
                .collect {
                    storeData.emit(it)
                }
        }
    }
}