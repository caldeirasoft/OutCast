package com.caldeirasoft.outcast.ui.screen.store.genre

import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreGroupingUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreTopChartsIdsUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreCollectionsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class StoreGenreViewModel(
    val genreId: Int,
    fetchStoreGroupingUseCase: FetchStoreGroupingUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    fetchStoreTopChartsIdsUseCase: FetchStoreTopChartsIdsUseCase
) : StoreCollectionsViewModel<StoreGroupingPage>(
    fetchStoreFrontUseCase = fetchStoreFrontUseCase,
    fetchStoreGroupingUseCase = fetchStoreGroupingUseCase,
    fetchStoreTopChartsIdsUseCase = fetchStoreTopChartsIdsUseCase
) {
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

    companion object {
        private const val GENRE_ID_SAVED_STATE_KEY = "GENRE_ID_SAVED_STATE_KEY"
        const val BUNDLE_ARGS = "BUNDLE_ARGS"
    }
}