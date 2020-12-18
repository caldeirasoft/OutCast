package com.caldeirasoft.outcast.ui.screen.store.storeroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDataUseCase
import com.caldeirasoft.outcast.domain.usecase.GetStoreItemsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent

@ExperimentalCoroutinesApi
class StoreRoomViewModel(
    private val room: StoreRoom,
    private val fetchStoreDataUseCase: FetchStoreDataUseCase,
    private val getStoreItemsUseCase: GetStoreItemsUseCase,
) : ViewModel(), KoinComponent {

    // screen state
    protected val storeScreenState = MutableStateFlow<ScreenState>(ScreenState.Idle)
    // genre resource
    private val storeResourceData = MutableStateFlow<Resource<StorePage>>(Resource.Loading())
    // genre map
    protected val storeData =  MutableStateFlow<StorePage?>(null)
    // state
    private val state: StateFlow<State> = MutableStateFlow(State())

    // paged list
    val storeDataPagedList: Flow<PagingData<StoreItem>> =
        getPagedList()
            .cachedIn(viewModelScope)

    private fun getPagedList(): Flow<PagingData<StoreItem>> =
        Pager(PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            maxSize = 100,
            prefetchDistance = 3
        )) {
            StoreDataPagingSource(scope = viewModelScope) {
                run {
                    if (room.url.isEmpty()) flowOf(Resource.Success(room.page))
                    else fetchStoreDataUseCase.executeAsync(room.url, room.storeFront)
                }.onEach { storeResourceData.emit(it) }
                    .filterIsInstance<Resource.Success<StorePage>>()
                    .map { it.data }
            }
        }.flow

    data class State(
        val screenState: ScreenState = ScreenState.Idle,
        val storeData: StorePage? = null,
    )
}