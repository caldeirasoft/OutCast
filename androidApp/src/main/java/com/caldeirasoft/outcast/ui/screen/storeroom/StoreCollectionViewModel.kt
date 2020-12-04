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
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDataUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.flow.*

class StoreCollectionViewModel(
    private val fetchStoreDataUseCase: FetchStoreDataUseCase,
    private val getStoreItemsUseCase: GetStoreItemsUseCase,
    private val room: StoreRoom
) : ViewModel()
{
    private val loadingData =
        MutableStateFlow<ScreenState>(ScreenState.Loading)

    private val storeData = MutableStateFlow<StoreRoom?>(null)

    val pagedList: Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                maxSize = 100,
                prefetchDistance = 3
            )
        ) {
            object : StoreDataPagingSource(getStoreItemsUseCase) {
                override fun getStoreData(): Flow<StoreData> =
                    getStoreDataFromStoreRoom(room)
            }

        }.flow
        .cachedIn(viewModelScope)


    fun getStoreDataFromStoreRoom(room: StoreRoom): Flow<StoreData> = flow {
        if (room.url.isNotEmpty())
            emitAll(fetchStoreDataUseCase.execute(room.url, room.storeFront))
        else
            emit(room)
    }
}