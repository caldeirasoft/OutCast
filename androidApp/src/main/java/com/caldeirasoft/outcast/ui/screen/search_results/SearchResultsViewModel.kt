package com.caldeirasoft.outcast.ui.screen.search_results

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.repository.*
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.screen.base.BaseViewModel
import com.caldeirasoft.outcast.ui.screen.base.SearchUiModel
import com.caldeirasoft.outcast.ui.screen.base.StoreUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class SearchResultsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val storeRepository: StoreRepository,
    val podcastsRepository: PodcastsRepository,
    private val searchRepository: SearchRepository,
    private val settingsRepository: SettingsRepository
) : BaseViewModel<SearchResultsViewModel.State, SearchResultsViewModel.Event, SearchResultsViewModel.Action>(
    initialState = State())
{
    private val followLoadingStatus: MutableStateFlow<List<Long>> =
        MutableStateFlow(emptyList())

    private val queryHintFlow = state
        .map { it.queryHint }
        .distinctUntilChanged()

    private val queryFlow = state
        .map { it.query }
        .distinctUntilChanged()

    // paged list
    @OptIn(FlowPreview::class)
    val searchResults: Flow<PagingData<StoreUiModel>> =
        queryFlow.map { query ->
            val storeFront = settingsRepository.storeFrontFlow.first()
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = {
                    StoreDataPagingSource(
                        loadDataFromNetwork = {
                            storeRepository.getSearchResultsAsync(query, storeFront)
                        },
                        dataLoadedCallback = null,
                        itemsLimit = null,
                        getStoreItems = { lookupIds, storeFront, storeData ->
                            storeRepository.getListStoreItemDataAsync(
                                lookupIds,
                                storeFront,
                                storeData
                            )
                        }
                    )
                }
            ).flow
        }.flattenMerge()
            .cachedIn(viewModelScope)


    @OptIn(FlowPreview::class)
    val localSearchPodcastsResults: Flow<PagingData<Podcast>> =
        getSearchResults(searchRepository::searchPodcasts)
            .cachedIn(viewModelScope)

    @OptIn(FlowPreview::class)
    val localSearchEpisodesResults: Flow<PagingData<Episode>> =
        getSearchResults(searchRepository::searchEpisodes)
            .cachedIn(viewModelScope)

    override fun activate() {
        podcastsRepository
            .getFollowedPodcastIds()
            .distinctUntilChanged()
            .setOnEach {
                copy(followingStatus = it)
            }

        queryHintFlow
            .map { query ->
                when {
                    query.isEmpty() ->
                        searchRepository
                            .getSearches()
                            .map { SearchUiModel.HistoryItem(it) as SearchUiModel }
                    else -> {
                        val historyResults = searchRepository.searchHistory(query)
                        val storeFront = settingsRepository.storeFrontFlow.first()
                        val hintResults = storeRepository.getSearchTermHintsAsync(query, storeFront)
                        historyResults
                            .map { SearchUiModel.HistoryItem(it) }
                            .plus(
                                hintResults
                                    .minus(historyResults)
                                    .map { SearchUiModel.HintItem(it) }
                            )
                    }
                }
            }
            .setOnEach {
                copy(hints = it)
            }

        followLoadingStatus.setOnEach { copy(followLoadingStatus = it) }
    }

    override suspend fun performAction(action: Action) = when (action) {
        is Action.Search ->
            if (action.isHint) searchHint(action.query)
            else search(action.query)
        is Action.OpenPodcastDetail -> emitEvent(Event.OpenPodcastDetail(action.podcast))
        is Action.OpenEpisodeDetail -> emitEvent(Event.OpenEpisodeDetail(action.episode))
        is Action.OpenStorePodcastDetail -> emitEvent(Event.OpenStorePodcastDetail(action.podcast))
        is Action.OpenStoreEpisodeDetail -> emitEvent(Event.OpenStoreEpisodeDetail(action.episode))
        is Action.Follow -> followPodcast(action.podcast)
        is Action.Exit -> emitEvent(Event.Exit)
    }

    fun search(term: String) {
        viewModelScope.launch {
            setState {
                copy(
                    queryHint = term,
                    query = term
                )
            }
            searchRepository.addToSearchHistory(term)
        }
    }

    fun searchHint(term: String) {
        viewModelScope.setState {
            copy(queryHint = term)
        }
    }

    fun followPodcast(item: StorePodcast) {
        viewModelScope.launch {
            runCatching {
                setPodcastFollowLoading(item, true)
                podcastsRepository.followPodcast(item, updatePodcast = true)
                delay(1000)
                setPodcastFollowLoading(item, false)
            }.onFailure {
                setPodcastFollowLoading(item, false)
            }
        }
    }

    private suspend fun setPodcastFollowLoading(item: StorePodcast, isLoading: Boolean) {
        if (isLoading)
            followLoadingStatus.emit(followLoadingStatus.value.plus(item.id))
        else
            followLoadingStatus.emit(followLoadingStatus.value.minus(item.id))
    }

    @OptIn(FlowPreview::class)
    private inline fun <reified T : Any> getSearchResults(
        crossinline searchFunction: (query: String) -> DataSource.Factory<Int, T>
    ) : Flow<PagingData<T>> =
        queryFlow.map { query ->
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = searchFunction(query).asPagingSourceFactory()
            ).flow
        }.flattenMerge()

    data class State(
        val queryHint: String = "",
        val query: String = "",
        val followingStatus: List<Long> = emptyList(),
        val followLoadingStatus: List<Long> = emptyList(),
        val podcasts: List<Podcast> = emptyList(),
        val episodes: List<Episode> = emptyList(),
        val hints: List<SearchUiModel> = emptyList()
    )

    sealed class Event {
        data class OpenPodcastDetail(val podcast: Podcast) : Event()
        data class OpenEpisodeDetail(val episode: Episode) : Event()
        data class OpenStorePodcastDetail(val podcast: StorePodcast) : Event()
        data class OpenStoreEpisodeDetail(val episode: StoreEpisode) : Event()
        object Exit : Event()
    }

    sealed class Action {
        data class Search(var query: String, var isHint: Boolean): Action()
        data class Follow(var podcast: StorePodcast): Action()
        data class OpenPodcastDetail(val podcast: Podcast) : Action()
        data class OpenEpisodeDetail(val episode: Episode) : Action()
        data class OpenStorePodcastDetail(val podcast: StorePodcast) : Action()
        data class OpenStoreEpisodeDetail(val episode: StoreEpisode) : Action()
        object Exit : Action()
    }
}