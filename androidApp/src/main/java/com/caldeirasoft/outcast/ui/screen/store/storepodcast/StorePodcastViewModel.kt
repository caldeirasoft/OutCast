package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.store.StorePodcastPage
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStorePodcastDataUseCase
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StorePodcastViewModel(url: String) : ViewModel(), KoinComponent {

    private val fetchStorePodcastDataUseCase: FetchStorePodcastDataUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()

    // storefront
    private val storeFront: Flow<String> = fetchStoreFrontUseCase.getStoreFront()
    // screen state
    private val storeScreenState = MutableStateFlow<ScreenState>(ScreenState.Loading)
    // genre map
    private val storeData:StateFlow<StorePodcastPage?> =
        storeFront.flatMapLatest {
            fetchStorePodcastDataUseCase.execute(url, it)
        }
            .onStart { storeScreenState.emit(ScreenState.Loading) }
            .onEach { storeScreenState.emit(ScreenState.Success) }
            .catch { storeScreenState.emit(ScreenState.Error(it)) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    // state
    val state = MutableStateFlow(State())

    init {
        combine(
            storeScreenState,
            storeData)
        { storeScreenState, storeData ->
            State(storeScreenState, storeData)
        }
            .onEach { state.emit(it) }
            .launchIn(viewModelScope)
    }

    data class State(
        val screenState: ScreenState = ScreenState.Idle,
        val storeData: StorePodcastPage? = null,
    )
}