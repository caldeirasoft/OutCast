package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
class StorePodcastViewModel(
    val initialState: StorePodcastViewState,
) : MavericksViewModel<StorePodcastViewState>(initialState), KoinComponent {
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()
    private val fetchStorePodcastDataUseCase: FetchStorePodcastDataUseCase by inject()
    private val loadPodcastUseCase: LoadPodcastUseCase by inject()
    private val loadPodcastRelatedPagingDataUseCase: LoadPodcastRelatedPagingDataUseCase by inject()
    private val loadFollowedPodcastsUseCase: LoadFollowedPodcastsUseCase by inject()
    private val subscribeUseCase: SubscribeUseCase by inject()
    private val unsubscribeUseCase: UnsubscribeUseCase by inject()

    val episodesStateFlow: MutableStateFlow<List<Episode>> = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            fetchPodcast()
        }

        // fetch local podcast data subscription
        loadPodcastUseCase.execute(initialState.podcastId)
            .setOnEach { copy(isSubscribed = it?.isSubscribed ?: false) }

        //loadFollowedPodcastsUseCase.execute()
        //    .setOnEach { list -> copy(isSubscribed = list.map { it.podcastId }.contains(initialState.podcastId)) }

        episodesStateFlow
            .setOnEach { copy(episodes = it) }
    }

    private suspend fun fetchPodcast() {
        withState { state ->
            state.podcastPageAsync.invoke()?.let { podcastPage ->

                // fetch remote podcast data
                suspend {
                    val storeFront = fetchStoreFrontUseCase.getStoreFront().first()
                    fetchStorePodcastDataUseCase.execute(url = podcastPage.podcast.url,
                        storeFront = storeFront)
                }.execute(retainValue = StorePodcastViewState::podcastPageAsync) {
                    copy(podcastPageAsync = it)
                }
            }
        }

        onAsync(
            StorePodcastViewState::podcastPageAsync,
            onSuccess = { podcastPage ->
                episodesStateFlow.tryEmit(podcastPage.episodes)
                loadPodcastRelatedPagingDataUseCase
                    .execute(viewModelScope, podcastPage)
                    .cachedIn(viewModelScope)
                    .setOnEach {
                        copy(otherPodcasts = it)
                    }
            }
        )
    }

    fun showAllEpisodes() {
        setState {
            copy(showAllEpisodes = true)
        }
    }

    fun subscribe() {
        withState { state ->
            state.podcastPageAsync.invoke()?.let { podcastPage ->
                subscribeUseCase.execute(podcastPage, NewEpisodesAction.INBOX)
                    .onStart {
                        setState { copy(isSubscribing = true) }
                    }
                    .setOnEach { copy(isSubscribing = false) }
            }
        }
    }

    fun unfollow() {
        unsubscribeUseCase.execute(initialState.podcastId)
    }
}