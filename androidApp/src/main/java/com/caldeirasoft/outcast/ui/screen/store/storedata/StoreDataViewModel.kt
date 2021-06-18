package com.caldeirasoft.outcast.ui.screen.store.storedata

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.data.util.StoreDataPagingSource
import com.caldeirasoft.outcast.domain.models.store.StoreCategory
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.usecase.FetchFollowedPodcastsUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FollowUseCase
import com.caldeirasoft.outcast.ui.screen.BaseViewModelEvents
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
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    private val fetchFollowedPodcastsUseCase: FetchFollowedPodcastsUseCase,
    val followUseCase: FollowUseCase,
    val storeRepository: StoreRepository,
    val podcastsRepository: PodcastsRepository,
    val dataStoreRepository: DataStoreRepository
) : BaseViewModelEvents<StoreDataState, StoreDataEvent>(

    initialState = StoreDataState(
        data = runCatching {
            val routeArgs = StoreRouteArgs.fromSavedStatedHandle(savedStateHandle)
            routeArgs.storeData.unserialize<StoreData>()
        }.getOrDefault(StoreData.Default)
    ))
{

    private val followLoadingStatus: MutableStateFlow<List<Long>> =
        MutableStateFlow(emptyList())

    init {

        podcastsRepository
            .getFollowedPodcastIds()
            .distinctUntilChanged()
            .setOnEach {
                copy(followingStatus = it)
            }

        followLoadingStatus.setOnEach { copy(followLoadingStatus = it) }
    }

    val urlFlow = state
        .map { it.url }
        .distinctUntilChanged()

    // paged list
    @OptIn(FlowPreview::class)
    val discover: Flow<PagingData<StoreUiModel>> =
        fetchStoreFrontUseCase.getStoreFront()
            .distinctUntilChanged()
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
            emitEvent(StoreDataEvent.OpenCategories(
                categories = it.categories,
                selectedCategoryId = it.currentCategoryId
            ))
        }
    }

    fun selectCategoryFilter(selectedCategory: StoreCategory) {
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
}