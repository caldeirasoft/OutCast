package com.caldeirasoft.outcast.ui.screen.search_results

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import com.caldeirasoft.outcast.data.repository.SearchRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.ui.screen.BaseViewModel
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
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    val storeRepository: StoreRepository,
    val podcastsRepository: PodcastsRepository,
    val dataStoreRepository: DataStoreRepository,
    val searchRepository: SearchRepository,
) : BaseViewModel<SearchResultsState>(
    initialState = SearchResultsState())
{

    private val followLoadingStatus: MutableStateFlow<List<Long>> =
        MutableStateFlow(emptyList())

    private val queryHintFlow = state
        .map { it.queryHint }
        .distinctUntilChanged()

    private val queryFlow = state
        .map { it.query }
        .distinctUntilChanged()

    private val storeFrontFlow = fetchStoreFrontUseCase
        .getStoreFront()
        .distinctUntilChanged()

    // paged list
    @OptIn(FlowPreview::class)
    val searchResults: Flow<PagingData<StoreUiModel>> =
        combine(queryFlow, storeFrontFlow) { query, storeFront ->
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

    init {
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
                        val storeFront = fetchStoreFrontUseCase.getStoreFront().first()
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
}