package com.caldeirasoft.outcast.ui.screen.store.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.store.StoreGenreData
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStoreGenreDataUseCase
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class StoreSearchViewModel(
    private val loadStoreGenreDataUseCase: LoadStoreGenreDataUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
) : ViewModel() {

    // screen state
    private val screenState = MutableStateFlow<ScreenState>(ScreenState.Loading)

    // storefront
    private val storeFront = fetchStoreFrontUseCase.getStoreFront()

    // store genre data
    protected val storeGenreData: Flow<StoreGenreData> =
        storeFront
            .map {
                screenState.emit(ScreenState.Loading)
                loadStoreGenreDataUseCase.execute(it)
            }
            .onEach {
                screenState.emit(ScreenState.Success)
            }

    // state
    val state: StateFlow<State> =
        combine(storeFront, storeGenreData, screenState) { storeFront, storeGenreData, screenState ->
                State(storeFront = storeFront, storeGenreData = storeGenreData, screenState = screenState)
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, State())


    data class State(
        val storeGenreData: StoreGenreData? = null,
        val storeFront: String? = null,
        val screenState: ScreenState = ScreenState.Idle
    )
}