package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.FetchStorePodcastDataUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadPodcastRelatedPagingDataUseCase
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
class StorePodcastViewModel(
    initialState: StorePodcastViewState,
) : MavericksViewModel<StorePodcastViewState>(initialState), KoinComponent {
    val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()
    val fetchStorePodcastDataUseCase: FetchStorePodcastDataUseCase by inject()
    val loadPodcastRelatedPagingDataUseCase: LoadPodcastRelatedPagingDataUseCase by inject()

    val episodesStateFlow: MutableStateFlow<List<Episode>> = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            fetchPodcast()
        }

        episodesStateFlow
            .setOnEach { copy(episodes = it) }
    }

    private suspend fun fetchPodcast() {
        withState { state ->
            state.podcastPageAsync.invoke()?.let { podcastPage ->
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
}