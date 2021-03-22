package com.caldeirasoft.outcast.ui.screen.store.directory

import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStoreDirectoryPagingDataUseCase
import com.caldeirasoft.outcast.domain.util.tryCast
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import com.caldeirasoft.outcast.ui.screen.store.base.FollowViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

@OptIn(KoinApiExtension::class)
@FlowPreview
@ExperimentalCoroutinesApi
class StoreDirectoryViewModel(
    initialState: StoreDirectoryViewState,
) : FollowViewModel<StoreDirectoryViewState>(initialState), KoinComponent {

    private val loadStoreDirectoryPagingDataUseCase: LoadStoreDirectoryPagingDataUseCase by inject()
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()

    // paged list
    val discover: Flow<PagingData<StoreItem>> =
        fetchStoreFrontUseCase.getStoreFront()
            .onEach { setState { copy(storeFront = it) } }
            .flatMapConcat { store ->
                loadStoreDirectoryPagingDataUseCase.executeAsync(
                    scope = viewModelScope,
                    storeFront = store,
                    newVersionAvailable = { Timber.d("DBG - New version available") },
                    dataLoadedCallback = {
                        it.tryCast<StoreGroupingPage> {
                            //this@StoreDirectoryViewModel.storeData.tryEmit(this)
                        }
                    })
            }
            .cachedIn(viewModelScope)

    override fun StoreDirectoryViewState.setPodcastFollowed(list: List<Podcast>): StoreDirectoryViewState =
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