package com.caldeirasoft.outcast.ui.screen.store.directory

import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage
import com.caldeirasoft.outcast.domain.usecase.FetchStoreDirectoryUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
@ExperimentalCoroutinesApi
class StoreDirectoryViewModel : StoreCollectionsViewModel<StoreGroupingPage>(), KoinComponent {
    private val fetchStoreDirectoryUseCase: FetchStoreDirectoryUseCase by inject()
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