package com.caldeirasoft.outcast.ui.screen.store

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreDataWithCollections
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StoreDirectoryViewModel @ViewModelInject constructor(
    private val fetchStoreDirectoryUseCase: FetchStoreDirectoryUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : StoreBaseViewModel() {

    var screenState: ScreenState by mutableStateOf(ScreenState.Idle)
        private set

    var storeGroupingData: StoreGroupingData? by mutableStateOf(null)
        private set

    init {
        viewModelScope.launch {
            fetchStoreDirectoryUseCase
                .invoke(FetchStoreDirectoryUseCase.Params(storeFront = storeFront))
                .onStart { screenState = ScreenState.Loading }
                .onCompletion { screenState = ScreenState.Success }
                .catch {
                    screenState = ScreenState.Error(it)
                }
                .collect { it ->
                    storeGroupingData = it
                }
        }
    }
}