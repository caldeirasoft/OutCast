package com.caldeirasoft.outcast.ui.screen.store.base

import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.usecase.FollowUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

abstract class FollowViewModel<S : MavericksState>(
    initialState: S,
    private val followUseCase: FollowUseCase,
    podcastDao: PodcastDao
) : MavericksViewModel<S>(initialState) {

    val followingStatus: MutableStateFlow<List<Long>> =
        MutableStateFlow(emptyList())
    val followLoadingStatus: MutableStateFlow<List<Long>> =
        MutableStateFlow(emptyList())

    init {
        podcastDao.getFollowedPodcastIds()
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