package com.caldeirasoft.outcast.ui.screen.storepodcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.StorePodcast
import com.caldeirasoft.outcast.domain.usecase.FetchStorePodcastDataUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import com.caldeirasoft.outcast.domain.util.DataState
import kotlinx.coroutines.flow.*

class StorePodcastViewModel(
    private val fetchStorePodcastDataUseCase: FetchStorePodcastDataUseCase,
    val getStoreItemsUseCase: GetStoreItemsUseCase,
) : ViewModel() {

    private val _storeDataState =
        MutableStateFlow<DataState<StorePodcast>>(DataState.Loading())
    val storeDataState: Flow<DataState<StorePodcast>> = _storeDataState

    fun loadData(url: String) {
        fetchStorePodcastDataUseCase.execute(url, "")
            .onStart { _storeDataState.emit(DataState.Loading()) }
            .map { DataState.Success(it) }
            .onEach { _storeDataState.emit(it) }
            .catch { _storeDataState.emit(DataState.Error(it)) }
            .launchIn(viewModelScope)
    }
}