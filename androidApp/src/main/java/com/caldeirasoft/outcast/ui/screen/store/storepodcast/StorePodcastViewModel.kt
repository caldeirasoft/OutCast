package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.data.util.StorePodcastPagingSource
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcastPage
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStorePodcastDataUseCase
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.screen.store.storeroom.StoreRoomViewModel
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopChartsViewModel
import com.caldeirasoft.outcast.ui.util.ScreenState
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StorePodcastViewModel(val storePodcast: StorePodcast) : ViewModel(), KoinComponent {

    private val fetchStorePodcastDataUseCase: FetchStorePodcastDataUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()

    // storefront
    private val storeFront: Flow<String> = fetchStoreFrontUseCase.getStoreFront()

    // store resource data
    protected val storeResourceData: StateFlow<Resource> =
        storeFront.flatMapLatest {
            fetchStorePodcastDataUseCase.execute(storePodcast.url, it)
        }
            .stateIn(viewModelScope, SharingStarted.Eagerly, Resource.Loading)

    // genre map
    private val storePage: Flow<StorePodcastPage> =
        storeResourceData
            .filterIsInstance<Resource.Success<StorePodcastPage>>()
            .map { it.data }

    // paged list
    val otherPodcasts: Flow<PagingData<StoreItem>> =
        storePage
            .map { getStoreDataPagedList(it) }
            .flattenMerge()
            .cachedIn(viewModelScope)

    // state
    val state = MutableStateFlow(State(
        storeResourceData = Resource.Loading,
        storePage = storePodcast.page))

    init {
        combine(storeResourceData, storePage)
        { storeResourceData, storePage ->
            State(storeResourceData, storePage)
        }
            .onEach { state.tryEmit(it) }
            .launchIn(viewModelScope)
    }

    private fun getStoreDataPagedList(storePodcastPage: StorePodcastPage): Flow<PagingData<StoreItem>> =
        Pager(
            PagingConfig(
                pageSize = 5,
                enablePlaceholders = false,
                maxSize = 100,
                prefetchDistance = 2
            )
        ) {
            StorePodcastPagingSource(
                scope = viewModelScope,
                storePodcast = storePodcastPage
            )
        }.flow


    data class State(
        val storeResourceData: Resource,
        val storePage: StorePodcastPage,
    )
}