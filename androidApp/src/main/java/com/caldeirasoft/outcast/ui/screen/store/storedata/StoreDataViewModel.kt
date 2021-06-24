package com.caldeirasoft.outcast.ui.screen.store.storedata

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.data.common.Constants
import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import com.caldeirasoft.outcast.data.repository.SettingsRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.models.store.StoreCategory
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.screen.base.BaseViewModel
import com.caldeirasoft.outcast.ui.screen.base.StoreUiModel
import com.caldeirasoft.outcast.ui.screen.store.storedata.args.StoreRouteArgs
import com.caldeirasoft.outcast.ui.util.unserialize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class StoreDataViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val storeRepository: StoreRepository,
    val podcastsRepository: PodcastsRepository,
    val settingsRepository: SettingsRepository,
) : BaseViewModel<StoreDataViewModel.State, StoreDataViewModel.Event, StoreDataViewModel.Action>(
    initialState = State(
        data = runCatching {
            val routeArgs = StoreRouteArgs.fromSavedStatedHandle(savedStateHandle)
            routeArgs.storeData.unserialize<StoreData>()
        }.getOrDefault(StoreData.Default)
    ))
{

    private val followLoadingStatus: MutableStateFlow<List<Long>> =
        MutableStateFlow(emptyList())

    override fun activate() {
        podcastsRepository
            .getFollowedPodcastIds()
            .distinctUntilChanged()
            .setOnEach {
                copy(followingStatus = it)
            }

        followLoadingStatus.setOnEach { copy(followLoadingStatus = it) }
    }

    override suspend fun performAction(action: Action) = when(action) {
        is Action.OpenPodcastDetail -> emitEvent(Event.OpenPodcastDetail(action.storePodcast))
        is Action.OpenEpisodeDetail -> emitEvent(Event.OpenEpisodeDetail(action.storeEpisode))
        is Action.OpenStoreData -> emitEvent(Event.OpenStoreData(action.storeData))
        is Action.SelectCategory -> filterByCategory(action.category)
        is Action.FollowPodcast -> followPodcast(action.storePodcast)
        is Action.Exit -> emitEvent(Event.Exit)
    }

    private val urlFlow = state
        .map { it.url }
        .distinctUntilChanged()

    // paged list
    @OptIn(FlowPreview::class)
    val discover: Flow<PagingData<StoreUiModel>> =
        settingsRepository
            .storeFrontFlow
            .combine(urlFlow) { storeFront, url ->
                Pager(
                    config = PagingConfig(
                        pageSize = 10,
                        enablePlaceholders = false,
                    ),
                    pagingSourceFactory = {
                        StoreDataPagingSource(
                            loadDataFromNetwork = {
                                when {
                                    url != null && url.isNotEmpty() -> storeRepository.getStoreDataAsync(
                                        url,
                                        storeFront
                                    )
                                    else -> initialState.storeData
                                }
                            },
                            dataLoadedCallback = { page ->
                                viewModelScope.setState {
                                    copy(
                                        storeData = page,
                                        title = page.label,
                                        categories = page.storeCategories
                                    )
                                }
                            },
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
            }
            .flattenMerge()
            .cachedIn(viewModelScope)


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

    private suspend fun openCategoriesDialog() {
        withState {
            emitEvent(Event.OpenCategories(
                categories = it.categories,
                selectedCategoryId = it.currentCategoryId
            ))
        }
    }

    fun filterByCategory(selectedCategory: StoreCategory) {
        viewModelScope.setState {
            copy(
                currentCategoryId = selectedCategory.id,
                url = selectedCategory.url
            )
        }
    }

    private suspend fun setPodcastFollowLoading(item: StorePodcast, isLoading: Boolean) {
        if (isLoading)
            followLoadingStatus.emit(followLoadingStatus.value.plus(item.id))
        else
            followLoadingStatus.emit(followLoadingStatus.value.minus(item.id))
    }

    data class State(
        val storeData: StoreData,
        val url: String? = null,
        val title: String = "",
        val followingStatus: List<Long> = emptyList(),
        val followLoadingStatus: List<Long> = emptyList(),
        val categories: List<StoreCategory> = emptyList(),
        val currentCategoryId: Int = Constants.DEFAULT_GENRE,
        val newVersionAvailable: Boolean = false,
    ) {
        constructor(data: StoreData) : this(storeData = data, url = data.url, title = data.label)

        val currentCategory: StoreCategory
            get() = categories.first { it.id == currentCategoryId }
    }

    sealed class Event {
        data class OpenPodcastDetail(val storePodcast: StorePodcast) : Event()
        data class OpenEpisodeDetail(val storeEpisode: StoreEpisode) : Event()
        data class OpenStoreData(val storeData: StoreData) : Event()
        data class OpenCategories(val categories: List<StoreCategory>, val selectedCategoryId: Int) : Event()
        data class FilterByCategory(val category: StoreCategory?) : Event()
        object Exit : Event()
    }

    sealed class Action {
        data class OpenPodcastDetail(val storePodcast: StorePodcast) : Action()
        data class OpenEpisodeDetail(val storeEpisode: StoreEpisode) : Action()
        data class OpenStoreData(val storeData: StoreData) : Action()
        data class FollowPodcast(val storePodcast: StorePodcast) : Action()
        data class SelectCategory(val category: StoreCategory): Action()
        object Exit : Action()
    }
}