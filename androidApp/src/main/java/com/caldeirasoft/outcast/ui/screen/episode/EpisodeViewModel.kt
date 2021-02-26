package com.caldeirasoft.outcast.ui.screen.episode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.usecase.FetchStoreEpisodeDataUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.*

class EpisodeViewModel(
    val storeEpisode: StoreEpisode,
    private val fetchStoreEpisodeDataUseCase: FetchStoreEpisodeDataUseCase,
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase,
) : ViewModel()
{
    // storefront
    private val storeFront: Flow<String> = fetchStoreFrontUseCase.getStoreFront()

    // store resource data
    protected val storeResourceData: StateFlow<Resource> =
        storeFront.flatMapLatest {
            fetchStoreEpisodeDataUseCase.execute(storeEpisode, it)
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, Resource.Loading)

    // genre map
    private val storeData: Flow<StoreEpisode> =
        storeResourceData
            .filterIsInstance<Resource.Success<StoreEpisode>>()
            .map { it.data }

    // state
    val state = MutableStateFlow(State(
        storeResourceData = Resource.Loading,
        storeEpisode = storeEpisode))

    init {
        combine(storeResourceData, storeData)
        { storeResourceData, storeData ->
            State(storeResourceData, storeData)
        }
            .onEach { state.tryEmit(it) }
            .launchIn(viewModelScope)
    }

    data class State(
        val storeResourceData: Resource,
        val storeEpisode: StoreEpisode,
    )
}