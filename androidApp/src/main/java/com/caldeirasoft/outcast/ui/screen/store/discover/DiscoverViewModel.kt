package com.caldeirasoft.outcast.ui.screen.store.discover

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModelFactory
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.di.hiltmavericks.AssistedViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.hiltMavericksViewModelFactory
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FollowUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStorePagingDataUseCase
import com.caldeirasoft.outcast.ui.screen.store.base.FollowViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
class DiscoverViewModel @AssistedInject constructor(
    @Assisted initialState: DiscoverState,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    private val loadStorePagingDataUseCase: LoadStorePagingDataUseCase,
    val followUseCase: FollowUseCase,
    val podcastDao: PodcastDao
) : FollowViewModel<DiscoverState>(initialState, followUseCase, podcastDao) {
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

    fun clearNewVersionNotification() {
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