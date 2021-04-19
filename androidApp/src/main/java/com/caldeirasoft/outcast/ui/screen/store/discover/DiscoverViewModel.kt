package com.caldeirasoft.outcast.ui.screen.store.discover

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.AssistedViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.hiltMavericksViewModelFactory
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadFollowedPodcastsUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStorePagingDataUseCase
import com.caldeirasoft.outcast.domain.usecase.SubscribeUseCase
import com.caldeirasoft.outcast.ui.screen.store.base.FollowViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
class DiscoverViewModel @AssistedInject constructor(
    @Assisted initialState: DiscoverState,
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    private val loadStorePagingDataUseCase: LoadStorePagingDataUseCase,
    val followUseCase: SubscribeUseCase,
    val loadFollowedPodcastsUseCase: LoadFollowedPodcastsUseCase,
) : FollowViewModel<DiscoverState>(initialState, followUseCase, loadFollowedPodcastsUseCase) {
    init {
        followingStatus.setOnEach { copy(followingStatus = it) }
    }

    // paged list
    @OptIn(FlowPreview::class)
    val discover: Flow<PagingData<StoreItem>> =
        fetchStoreFrontUseCase.getStoreFront()
            .map { storeFront ->
                loadStorePagingDataUseCase.executeAsync(
                    storeData = initialState.storeData,
                    storeFront = storeFront,
                    newVersionAvailable = {
                        setState {
                            copy(newVersionAvailable = true)
                        }
                    },
                    dataLoadedCallback = { page ->
                        setState {
                            copy(storePage = page, title = page.label)
                        }
                    })
            }
            .flattenMerge()
            .cachedIn(viewModelScope)

    fun clearNewVersionButton() {
        setState {
            copy(newVersionAvailable = false)
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<DiscoverViewModel, DiscoverState> {
        override fun create(initialState: DiscoverState): DiscoverViewModel
    }

    companion object :
        MavericksViewModelFactory<DiscoverViewModel, DiscoverState> by hiltMavericksViewModelFactory()
}