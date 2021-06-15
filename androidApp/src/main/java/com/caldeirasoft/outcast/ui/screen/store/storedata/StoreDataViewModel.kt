package com.caldeirasoft.outcast.ui.screen.store.storedata

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreCategory
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.usecase.FetchFollowedPodcastsUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FollowUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStorePagingDataUseCase
import com.caldeirasoft.outcast.ui.screen.BaseViewModelEvents
import com.caldeirasoft.outcast.ui.screen.store.storedata.args.StoreRouteArgs
import com.caldeirasoft.outcast.ui.util.getObject
import com.caldeirasoft.outcast.ui.util.unserialize
import cz.levinzonr.router.core.RouteArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class StoreDataViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    private val loadStorePagingDataUseCase: LoadStorePagingDataUseCase,
    private val fetchFollowedPodcastsUseCase: FetchFollowedPodcastsUseCase,
    val followUseCase: FollowUseCase,
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

        fetchFollowedPodcastsUseCase
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
    val discover: Flow<PagingData<StoreItem>> =
        fetchStoreFrontUseCase.getStoreFront()
            .distinctUntilChanged()
            .combine(urlFlow) { storeFront, url ->
                loadStorePagingDataUseCase.executeAsync(
                    url = url.orEmpty(),
                    storeData = initialState.storeData,
                    storeFront = storeFront,
                    dataLoadedCallback = { page ->
                        viewModelScope.setState {
                            copy(storeData = page,
                                title = page.label,
                                categories = page.storeCategories)
                        }
                    })
            }
            .flattenMerge()
            .cachedIn(viewModelScope)


    private fun clearNewVersionNotification() {
        viewModelScope.setState {
            copy(newVersionAvailable = false)
        }
    }

    fun followPodcast(item: StorePodcast) {
        followUseCase.execute(item)
            .onStart { setPodcastFollowLoading(item, true) }
            .catch { setPodcastFollowLoading(item, false) }
            .onEach {
                delay(1000)
                setPodcastFollowLoading(item, false)
            }
            .launchIn(viewModelScope)
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