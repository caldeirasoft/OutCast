package com.caldeirasoft.outcast.ui.screen.episode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.data.util.StorePodcastPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcastPage
import com.caldeirasoft.outcast.domain.usecase.FetchStoreEpisodeDataUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStorePodcastDataUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.screen.store.storeroom.StoreRoomViewModel
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopChartsViewModel
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class EpisodeViewModel(val storeEpisode: StoreEpisode) : ViewModel(), KoinComponent
{
    private val fetchStoreEpisodeDataUseCase: FetchStoreEpisodeDataUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()

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