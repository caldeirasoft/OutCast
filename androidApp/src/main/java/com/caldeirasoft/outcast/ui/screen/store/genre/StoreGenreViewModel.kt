package com.caldeirasoft.outcast.ui.screen.store.genre

import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage
import com.caldeirasoft.outcast.domain.usecase.FetchStoreGroupingUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreCollectionsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
@ExperimentalCoroutinesApi
class StoreGenreViewModel(private val genreId: Int)
    : StoreCollectionsViewModel<StoreGroupingPage>(), KoinComponent {
    private val fetchStoreGroupingUseCase: FetchStoreGroupingUseCase by inject()
    // state
    val state: StateFlow<State> =
        storeData
            .map { State(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, State())

    override fun getStoreDataFlow(): Flow<Resource> =
        storeFront
            .flatMapConcat {
                fetchStoreGroupingUseCase.executeAsync(genreId = genreId, storeFront = it)
            }


    data class State(
        val storeData: StoreGroupingPage? = null,
    )
}