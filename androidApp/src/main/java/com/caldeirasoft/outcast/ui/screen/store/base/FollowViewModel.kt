package com.caldeirasoft.outcast.ui.screen.store.base

import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.usecase.FetchFollowedPodcastsUseCase
import com.caldeirasoft.outcast.domain.usecase.FollowUseCase
import com.caldeirasoft.outcast.ui.screen.MvieViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

abstract class FollowViewModel<State: Any, Event: Any, Action: Any>(
    initialState: State,
    private val followUseCase: FollowUseCase,
    private val fetchFollowedPodcastsUseCase: FetchFollowedPodcastsUseCase,
) : MvieViewModel<State, Event, Action>(initialState) {

    val followingStatus: MutableStateFlow<List<Long>> =
        MutableStateFlow(emptyList())
    val followLoadingStatus: MutableStateFlow<List<Long>> =
        MutableStateFlow(emptyList())

    init {
        fetchFollowedPodcastsUseCase
            .getFollowedPodcastIds()
            .onEach { followingStatus.emit(it) }
            .launchIn(viewModelScope)
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

    private suspend fun setPodcastFollowLoading(item: StorePodcast, isLoading: Boolean) {
        if (isLoading)
            followLoadingStatus.emit(followLoadingStatus.value.plus(item.id))
        else
            followLoadingStatus.emit(followLoadingStatus.value.minus(item.id))
    }
}