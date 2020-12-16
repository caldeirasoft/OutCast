package com.caldeirasoft.outcast.ui.screen.store.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
abstract class StoreRoomBaseViewModel <T : StorePage> : ViewModel()
{
    protected val storeScreenState = MutableStateFlow<ScreenState>(ScreenState.Idle)

    protected val storeResourceData =
        MutableStateFlow<Resource<StorePage>>(Resource.Loading())

    protected val storeData: StateFlow<T?> =
        storeResourceData
            .onEach {
                when (it) {
                    is Resource.Loading -> storeScreenState.tryEmit(ScreenState.Loading)
                    is Resource.Error -> storeScreenState.tryEmit(ScreenState.Error(it.throwable))
                    is Resource.Success -> storeScreenState.tryEmit(ScreenState.Success)
                }
            }
            .filterIsInstance<Resource.Success<T>>()
            .map { it.data }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val storeDataPagedList: Flow<PagingData<StoreItem>> =
        storeData
            .filterNotNull()
            .flatMapLatest { getStoreDataPagedList(it) }
            .cachedIn(viewModelScope)

    protected val storeDataState: StateFlow<State<T>>

    private fun getStoreDataPagedList(storePage: StorePage): Flow<PagingData<StoreItem>> =
        Pager(getPagingConfig()) {
            StoreDataPagingSource(storeData = storePage, scope = viewModelScope)
        }.flow

    abstract fun getPagingConfig(): PagingConfig

    init {
        storeDataState = combine(storeScreenState, storeData)
        { storeScreenState, storeData -> State(storeScreenState, storeData) }
            .stateIn(viewModelScope, SharingStarted.Lazily, State())
    }

    data class State<T>(
        val screenState: ScreenState = ScreenState.Idle,
        val storeData: T? = null,
    )
}


