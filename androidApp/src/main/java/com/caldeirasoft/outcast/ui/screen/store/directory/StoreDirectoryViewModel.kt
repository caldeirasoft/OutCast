package com.caldeirasoft.outcast.ui.screen.store.directory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreGenre
import com.caldeirasoft.outcast.domain.models.store.StoreGenreData
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingData
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreGenresUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreGroupingUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@ExperimentalCoroutinesApi
class StoreDirectoryViewModel : ViewModel(), KoinComponent {
    private val fetchStoreGroupingUseCase: FetchStoreGroupingUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()

    // storefront
    private val storeFront = fetchStoreFrontUseCase.getStoreFront()
    // screen state
    private val storeScreenState = MutableStateFlow<ScreenState>(ScreenState.Success)
    // selected genre
    private val selectedGenre = MutableStateFlow<Int?>(null)
    // state
    val state = MutableStateFlow<State>(State())

    // paged list
    val discover: Flow<PagingData<StoreItem>> =
        combine(storeFront, selectedGenre) { storeFront, selectedGenre ->
            getStoreDataPagedList(storeFront = storeFront, storeGenre = selectedGenre)
        }
            .flattenMerge()
            .cachedIn(viewModelScope)


    init {
        combine(storeScreenState, selectedGenre)
        { storeScreenState, selectedGenre ->
            State(storeScreenState, selectedGenre)
        }
            .onEach { state.emit(it) }
            .launchIn(viewModelScope)
    }

    fun onGenreDiscoverSelected(storeGenre: Int) {
        //if not selected
        val selected = selectedGenre.value
        if (storeGenre == selected) {
            selectedGenre.tryEmit(null)
        } else {
            selectedGenre.tryEmit(storeGenre)
        }
    }

    private fun getStoreDataPagedList(storeGenre: Int?, storeFront: String): Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 3,
                enablePlaceholders = false,
                maxSize = 100,
                prefetchDistance = 2
            )
        ) {
            StoreDataPagingSource(scope = viewModelScope) {
                fetchStoreGroupingUseCase.executeAsync(storeGenre = storeGenre, storeFront = storeFront)
                    .filterIsInstance<Resource.Success<StoreData>>()
                    .map { it.data }
            }
        }.flow

    data class State(
        val screenState: ScreenState = ScreenState.Idle,
        val selectedGenre: Int? = null
    )
}