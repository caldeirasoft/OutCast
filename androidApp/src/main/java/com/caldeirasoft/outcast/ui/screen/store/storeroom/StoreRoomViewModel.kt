package com.caldeirasoft.outcast.ui.screen.store.storeroom

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.interfaces.StoreFeaturedPage
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.usecase.FetchStoreRoomPagingDataUseCase
import com.caldeirasoft.outcast.domain.util.tryCast
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import com.caldeirasoft.outcast.ui.screen.store.base.FollowViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
@ExperimentalCoroutinesApi
class StoreRoomViewModel(
    initialState: StoreRoomViewState,
) : FollowViewModel<StoreRoomViewState>(initialState), KoinComponent {

    private val fetchStoreRoomPagingDataUseCase: FetchStoreRoomPagingDataUseCase by inject()

    // paged list
    val discover: Flow<PagingData<StoreItem>> =
        fetchStoreRoomPagingDataUseCase.executeAsync(
            scope = viewModelScope,
            storeRoom = initialState.room,
            dataLoadedCallback = { page ->
                page.tryCast<StoreFeaturedPage> {
                    setState { copy(storePage = this@tryCast) }
                }
            })
            .cachedIn(viewModelScope)

    override fun StoreRoomViewState.setPodcastFollowed(list: List<Podcast>): StoreRoomViewState =
        list.map { it.podcastId }
            .let { ids ->
                val mapStatus = followingStatus.filter { it.value == FollowStatus.FOLLOWING }
                    .plus(ids.map { it to FollowStatus.FOLLOWED })
                copy(followingStatus = mapStatus)
            }

    override fun setPodcastFollowing(item: StorePodcast) {
        setState { copy(followingStatus = followingStatus.plus(item.podcast.podcastId to FollowStatus.FOLLOWING)) }
    }

    override fun setPodcastUnfollowed(item: StorePodcast) {
        setState {
            copy(followingStatus = followingStatus.filter { (it.key == item.podcast.podcastId && it.value == FollowStatus.FOLLOWING).not() })
        }
    }
}