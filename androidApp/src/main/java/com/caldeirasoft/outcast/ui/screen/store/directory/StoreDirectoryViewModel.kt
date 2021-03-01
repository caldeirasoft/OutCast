package com.caldeirasoft.outcast.ui.screen.store.directory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDirectoryUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class StoreDirectoryViewModel(
    private val fetchStoreDirectoryUseCase: FetchStoreDirectoryUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
) : ViewModel() {
    // storefront
    private val storeFront = fetchStoreFrontUseCase.getStoreFront()

    // paged list
    val discover: Flow<PagingData<StoreItem>> =
        getStoreDataPagedList()
            .cachedIn(viewModelScope)

    // state
    val state: StateFlow<State> =
        storeFront
            .map { State(storeFront = it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, State())

    private fun getStoreDataPagedList(): Flow<PagingData<StoreItem>> =
        storeFront
            .flatMapConcat {
                fetchStoreDirectoryUseCase.executeAsync(
                    storeFront = it,
                    dataLoadedCallback = null
                )
            }

    data class State(
        val storeData: StoreGroupingPage? = null,
        val storeFront: String? = null,
    )
}