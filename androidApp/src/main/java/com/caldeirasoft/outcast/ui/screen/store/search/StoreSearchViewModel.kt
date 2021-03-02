package com.caldeirasoft.outcast.ui.screen.store.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDirectoryUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class StoreSearchViewModel(
    private val fetchStoreDirectoryUseCase: FetchStoreDirectoryUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
) : ViewModel() {
    // storefront
    private val storeFront = fetchStoreFrontUseCase.getStoreFront()

    // store data
    protected val storeData: Flow<StoreGroupingPage> =
        storeFront
            .map {
                fetchStoreDirectoryUseCase.executeAsync(viewModelScope, it)
            }

    // state
    val state: StateFlow<State> =
        storeFront
            .combine(storeData) { storeFront, storeData ->
                State(storeFront = storeFront, storeData = storeData)
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, State())


    data class State(
        val storeData: StoreGroupingPage? = null,
        val storeFront: String? = null,
    )
}