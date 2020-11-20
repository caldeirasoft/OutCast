package com.caldeirasoft.outcast.ui.screen.storedata

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.StoreGroupingData
import com.caldeirasoft.outcast.domain.models.StoreMultiRoom
import com.caldeirasoft.outcast.domain.models.StoreRoom
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.ui.screen.storedirectory.StoreBaseViewModel
import com.caldeirasoft.outcast.ui.util.DataState
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StoreDataViewModel @ViewModelInject constructor(
    private val fetchStoreDataUseCase: FetchStoreDataUseCase,
    private val fetchStoreItemsUseCase: FetchStoreItemsUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : StoreBaseViewModel()
{
    private var urlChannel = Channel<String>()
    private val storeDataChannel: Channel<StoreData> = Channel()

    val storeDataStateFlow: Flow<DataState<StoreData>> =
        storeDataChannel
            .consumeAsFlow()
            .flowOn(Dispatchers.Main)
            .map { DataState.Success(it) as DataState<StoreGroupingData> }
            .catch { emit(DataState.Error(it)) }
            .onCompletion { }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.Eagerly, DataState.Loading())

    fun fetchData(url: String) {
        urlChannel.offer(url)
    }

    fun fetchData(title: String, ids: List<Long>) {
        storeDataChannel.offer(
            StoreRoom(
                id = 0,
                label = title,
                storeIds = ids,
            ))
    }

    fun getPager(storeRoom: StoreRoom): Pager<Int, StoreItem> =
        Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                maxSize = 200,
                prefetchDistance = 10
            ),
            pagingSourceFactory = {
                StoreDataItemsPagingSource(
                    storeRoom.storeIds,
                    storeRoom,
                    storeFront,
                    fetchStoreItemsUseCase)
            }
        )

    fun getPager(storeMultiRoom: StoreMultiRoom): Pager<Int, StoreCollection> =
        Pager(
            config = PagingConfig(
                pageSize = 3,
                enablePlaceholders = false,
                maxSize = 100,
                prefetchDistance = 2
            ),
            pagingSourceFactory = {
                StoreDataCollectionPagingSource(
                    storeMultiRoom,
                    storeFront,
                    fetchStoreItemsUseCase
                )
            }
        )

    fun getPagingSourceFactory(storeRoom: StoreRoom) : PagingSource<Int, StoreItem> =
        StoreDataItemsPagingSource(
            storeRoom.storeIds,
            storeRoom,
            storeFront,
            fetchStoreItemsUseCase)

    init {
        viewModelScope.launch {
            urlChannel
                .broadcast()
                .asFlow()
                .map {
                    fetchStoreDataUseCase
                        .invoke(FetchStoreDataUseCase.Params(url = it, storeFront = storeFront))
                }
                .collect { storeDataChannel.send(it) }
        }

    }
}