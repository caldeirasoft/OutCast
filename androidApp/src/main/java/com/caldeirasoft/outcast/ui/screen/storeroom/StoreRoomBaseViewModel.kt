package com.caldeirasoft.outcast.ui.screen.storeroom

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.StoreRoom
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.flow.*

abstract class StoreRoomBaseViewModel(
    private val getStoreItemsUseCase: GetStoreItemsUseCase,
) : ViewModel() {
    private val loadingData =
        MutableStateFlow<ScreenState>(ScreenState.Loading)

    private val storeData = MutableStateFlow<StoreRoom?>(null)

    val pagedList: Flow<PagingData<StoreItem>> = flowOf(
        storeData
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest {
                Log.d("pagedList", it.url)
                getCollectionPagedList(it)
            }
    ).flattenMerge()
        .cachedIn(viewModelScope)

    fun loadData(room: StoreRoom) {
        Log.d("pagedList - load", room.toString())
        Log.d("pagedList - load2", storeData.value.toString())
        storeData.tryEmit(room)
    }

    abstract suspend fun getStoreDataFromStoreRoom(room: StoreRoom): StoreData

    abstract fun getCollectionPagedList(storeRoom: StoreRoom): Flow<PagingData<StoreItem>>}
