package com.caldeirasoft.outcast.ui.screen.storedirectory

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreDataWithCollections
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.screen.storedata.StoreDataCollectionPagingSource
import com.caldeirasoft.outcast.ui.screen.storedata.StoreDataItemsPagingSource
import com.caldeirasoft.outcast.ui.util.DataState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.channels.receiveOrNull
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.flow.*

class StoreDirectoryViewModel @ViewModelInject constructor(
    private val fetchStoreDirectoryUseCase: FetchStoreDirectoryUseCase,
    private val fetchStoreItemsUseCase: FetchStoreItemsUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : StoreBaseViewModel() {

    private val pendingActions = Channel<Action>(Channel.UNLIMITED)

    var storeGroupingData: StateFlow<DataState<StoreGroupingData>> =
        fetchStoreDirectoryUseCase
            .invoke(FetchStoreDirectoryUseCase.Params(storeFront = storeFront))
            .flowOn(Dispatchers.Main)
            .map { DataState.Success(it) as DataState<StoreGroupingData> }
            .catch {
                println("emit!")
                emit(DataState.Error(it))
            }
            .onCompletion { }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.Eagerly, DataState.Loading())


    fun getPagingSourceFactory(storeDataWithCollections: StoreDataWithCollections)
            : PagingSource<Int, StoreCollection> =
        StoreDataCollectionPagingSource(
            storeDataWithCollections,
            storeFront,
            fetchStoreItemsUseCase
        )


    init {
    }

    sealed class Action {
        object Init : Action()
        object Refresh : Action()
    }

    override fun onCleared() {
        pendingActions.close()
    }
}