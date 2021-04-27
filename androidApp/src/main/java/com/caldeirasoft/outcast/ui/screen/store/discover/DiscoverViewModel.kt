package com.caldeirasoft.outcast.ui.screen.store.discover

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModelFactory
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.di.hiltmavericks.AssistedViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.hiltMavericksViewModelFactory
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.usecase.FetchFollowedPodcastsUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FollowUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStorePagingDataUseCase
import com.caldeirasoft.outcast.ui.navigation.getObject
import com.caldeirasoft.outcast.ui.screen.store.base.FollowViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class DiscoverViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    private val loadStorePagingDataUseCase: LoadStorePagingDataUseCase,
    private val fetchFollowedPodcastsUseCase: FetchFollowedPodcastsUseCase,
    val followUseCase: FollowUseCase,
    val podcastDao: PodcastDao
) : FollowViewModel<DiscoverState, DiscoverEvent, DiscoverActions>(
    initialState = DiscoverState(storeData = savedStateHandle.getObject("storeData")),
    followUseCase = followUseCase,
    fetchFollowedPodcastsUseCase = fetchFollowedPodcastsUseCase) {

    init {
        followingStatus.setOnEach { copy(followingStatus = it) }
        followLoadingStatus.setOnEach { copy(followLoadingStatus = it) }
    }

    // paged list
    @OptIn(FlowPreview::class)
    val discover: Flow<PagingData<StoreItem>> =
        fetchStoreFrontUseCase.getStoreFront()
            .distinctUntilChanged()
            .map { storeFront ->
                loadStorePagingDataUseCase.executeAsync(
                    storeData = initialState.storeData,
                    storeFront = storeFront,
                    newVersionAvailable = {
                        viewModelScope.setState {
                            copy(newVersionAvailable = true)
                        }
                    },
                    dataLoadedCallback = { page ->
                        viewModelScope.setState {
                            copy(storePage = page, title = page.label)
                        }
                    })
            }
            .flattenMerge()
            .cachedIn(viewModelScope)

    override suspend fun performAction(action: DiscoverActions) = when(action){
        is DiscoverActions.ClearNotificationNewVersionAvailable -> clearNewVersionNotification()
        is DiscoverActions.FollowPodcast -> followPodcast(action.storePodcast)
        else -> Unit
    }

    fun clearNewVersionNotification() {
        viewModelScope.setState {
            copy(newVersionAvailable = false)
        }
    }
}