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
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.StoreRoom
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.ui.screen.storedirectory.StoreBaseViewModel
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StoreDataViewModel @ViewModelInject constructor(
    private val fetchStoreDataUseCase: FetchStoreDataUseCase,
    private val fetchStoreItemsUseCase: FetchStoreItemsUseCase,
    @Assisted private val savedStateHandle: SavedStateHandle
) : StoreBaseViewModel() {

    var screenState: ScreenState by mutableStateOf(ScreenState.Idle)
        private set

    var storeData: StoreData? by mutableStateOf(null)
        private set

    fun fetchGrouping(url: String) {
        viewModelScope.launch {
            fetchStoreDataUseCase
                .invoke(FetchStoreDataUseCase.Params(url = url, storeFront = storeFront))
                .onStart { screenState = ScreenState.Loading }
                .onCompletion { screenState = ScreenState.Success }
                .catch {
                    screenState = ScreenState.Error(it)
                }
                .collect { it ->
                    storeData = it
                }
        }
    }

    fun getPager(storeRoom: StoreRoom): Pager<Int, StoreItem> =
        Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false,
                maxSize = 200,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                StoreDataItemsPagingSource(storeRoom.storeIds, storeRoom, storeFront, fetchStoreItemsUseCase)
            }
        )


    fun getStoreItemsPaged(storeRoom: StoreRoom) : PagingSource<Int, StoreItem> =
        StoreDataItemsPagingSource(storeRoom.storeIds, storeRoom, storeFront, fetchStoreItemsUseCase)
}