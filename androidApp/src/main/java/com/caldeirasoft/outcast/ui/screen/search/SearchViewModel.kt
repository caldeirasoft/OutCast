package com.caldeirasoft.outcast.ui.screen.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.db.entities.PodcastWithCount
import com.caldeirasoft.outcast.data.repository.*
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.models.store.StoreData.Companion.toStoreData
import com.caldeirasoft.outcast.ui.screen.base.BaseViewModel
import com.caldeirasoft.outcast.ui.screen.base.SearchUiModel
import com.caldeirasoft.outcast.ui.screen.base.StoreUiModel
import com.caldeirasoft.outcast.ui.screen.episodelist.BaseEpisodeListViewModel
import com.caldeirasoft.outcast.ui.screen.episodelist.EpisodeListViewModel
import com.caldeirasoft.outcast.ui.screen.episodelist.EpisodeUiModel
import com.caldeirasoft.outcast.ui.screen.search_results.SearchResultsViewModel
import com.caldeirasoft.outcast.ui.screen.store.storedata.StoreDataViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val storeRepository: StoreRepository,
    val podcastsRepository: PodcastsRepository,
    private val searchRepository: SearchRepository,
    private val settingsRepository: SettingsRepository
) : BaseViewModel<SearchViewModel.State, SearchViewModel.Event, SearchViewModel.Action>(
    initialState = State())
{
    override fun activate() {
        settingsRepository.storeFrontFlow
            .map { storeFront ->
                storeRepository.getStoreGenreDataAsync(storeFront)
            }
            .setOnEach {
                copy(
                    root = it.root,
                    genres = it.genres
                )
            }
    }

    override suspend fun performAction(action: Action) = when (action) {
        is Action.OpenStoreData ->
            emitEvent(Event.OpenStoreData(action.storeData))
        is Action.OpenTopCharts ->
            emitEvent(Event.OpenStoreData(StoreData.TopCharts))
        is Action.OpenStoreCategory ->
            emitEvent(Event.OpenStoreData(action.storeGenre.toStoreData()))
        is Action.OpenSearchResults -> emitEvent(Event.OpenSearchResults)
        is Action.Exit -> emitEvent(Event.Exit)
    }

    data class State(
        val root: StoreGenre? = null,
        val genres: List<StoreGenre> = emptyList()
    )

    sealed class Event {
        data class OpenStoreData(val storeData: StoreData) : Event()
        data class OpenStoreCategory(val storeGenre: StoreGenre) : Event()
        object OpenTopCharts : Event()
        object OpenSearchResults : Event()
        object Exit : Event()
    }

    sealed class Action {
        data class OpenStoreData(val storeData: StoreData) : Action()
        data class OpenStoreCategory(val storeGenre: StoreGenre) : Action()
        object OpenTopCharts : Action()
        object OpenSearchResults : Action()
        object Exit : Action()
    }
}