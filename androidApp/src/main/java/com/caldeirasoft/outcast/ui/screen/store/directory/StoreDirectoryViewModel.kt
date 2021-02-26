package com.caldeirasoft.outcast.ui.screen.store.directory

import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDirectoryUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreGroupingUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreTopChartsIdsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class StoreDirectoryViewModel(
    private val fetchStoreDirectoryUseCase: FetchStoreDirectoryUseCase,
    fetchStoreGroupingUseCase: FetchStoreGroupingUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    fetchStoreTopChartsIdsUseCase: FetchStoreTopChartsIdsUseCase
) : StoreCollectionsViewModel<StoreGroupingPage>(
    fetchStoreFrontUseCase = fetchStoreFrontUseCase,
    fetchStoreGroupingUseCase = fetchStoreGroupingUseCase,
    fetchStoreTopChartsIdsUseCase = fetchStoreTopChartsIdsUseCase
) {
    // state
    val state = MutableStateFlow(State())

    init {
        combine(
            storeData, storeFront)
        { storeData, storeFront ->
            State(storeData, storeFront)
        }
            .onEach { state.emit(it) }
            .launchIn(viewModelScope)
    }

    override fun getStoreDataFlow(): Flow<Resource> =
        storeFront
            .flatMapConcat { fetchStoreDirectoryUseCase.executeAsync(storeFront = it) }


    data class State(
        val storeData: StoreGroupingPage? = null,
        val storeFront: String? = null,
    )
}