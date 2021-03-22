package com.caldeirasoft.outcast.ui.screen.store.base

import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.usecase.LoadFollowedPodcastsUseCase
import com.caldeirasoft.outcast.domain.usecase.SubscribeUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
abstract class FollowViewModel<S : FollowState>(initialState: S) :
    MavericksViewModel<S>(initialState), KoinComponent {

    private val followUseCase: SubscribeUseCase by inject()
    private val loadFollowedPodcastsUseCase: LoadFollowedPodcastsUseCase by inject()

    init {
        loadFollowedPodcastsUseCase.execute()
            .setOnEach { setPodcastFollowed(it) }
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

    abstract fun S.setPodcastFollowed(list: List<Podcast>): S

    abstract fun setPodcastFollowing(item: StorePodcast)

    abstract fun setPodcastUnfollowed(item: StorePodcast)
}