package com.caldeirasoft.outcast.ui.screen.store.directory

import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingData
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDirectoryUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
@ExperimentalCoroutinesApi
class StoreDirectoryViewModel : StoreCollectionsViewModel<StoreGroupingData>(), KoinComponent {
    private val fetchStoreDirectoryUseCase: FetchStoreDirectoryUseCase by inject()
    // state
    val state = MutableStateFlow(State())

    init {
        combine(
            storeResourceData,
            storeFront)
        { storeResourceData, storeFront ->
            State(storeResourceData, storeFront)
        }
            .onEach { state.emit(it) }
            .launchIn(viewModelScope)
    }

    override fun getStoreDataFlow(): Flow<StoreData> =
        storeFront
            .flatMapConcat {
                fetchStoreDirectoryUseCase
                    .executeAsync(storeFront = it)
            }
            .filterIsInstance<Resource.Success<StoreData>>()
            .map { it.data }

    data class State(
        val storeResourceData: Resource = Resource.Loading,
        val storeFront: String? = null,
    )
}