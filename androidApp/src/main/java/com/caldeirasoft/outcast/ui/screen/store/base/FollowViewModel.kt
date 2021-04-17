package com.caldeirasoft.outcast.ui.screen.store.base

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.usecase.LoadFollowedPodcastsUseCase
import com.caldeirasoft.outcast.domain.usecase.SubscribeUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
abstract class FollowViewModel<S : MavericksState>(initialState: S) :
    MavericksViewModel<S>(initialState), KoinComponent {

    private val followUseCase: SubscribeUseCase by inject()
    private val loadFollowedPodcastsUseCase: LoadFollowedPodcastsUseCase by inject()

    val followingStatus: MutableStateFlow<Map<String, FollowStatus>> =
        MutableStateFlow(emptyMap())

    init {
        loadFollowedPodcastsUseCase.execute()
            .map { it.map { it.feedUrl } }
            .map { ids ->
                val mapStatus = followingStatus.value.filter { it.value == FollowStatus.FOLLOWING }
                    .plus(ids.map { it to FollowStatus.FOLLOWED })
                mapStatus
            }
            .onEach { followingStatus.emit(it) }
            .launchIn(viewModelScope)
    }

    fun subscribeToPodcast(item: StorePodcast) {
        followUseCase.execute(item)
            .onStart { setPodcastFollowing(item) }
            .catch { setPodcastUnfollowed(item) }
            .onEach {
                delay(1000)
                setPodcastUnfollowed(item)
            }
            .launchIn(viewModelScope)
    }

    suspend fun setPodcastFollowing(item: StorePodcast) {
        followingStatus.emit(followingStatus.value.plus(item.podcast.feedUrl to FollowStatus.FOLLOWING))
    }

    suspend fun setPodcastUnfollowed(item: StorePodcast) {
        followingStatus.emit(followingStatus.value.filter { (it.key == item.podcast.feedUrl && it.value == FollowStatus.FOLLOWING).not() })
    }
}