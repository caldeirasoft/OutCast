package com.caldeirasoft.outcast.ui.screen.podcast

import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadPodcastEpisodesUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadPodcastUseCase
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PodcastViewModel(
    initialState: PodcastViewState,
    val fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    val loadPodcastUseCase: LoadPodcastUseCase,
    val loadPodcastEpisodesUseCase: LoadPodcastEpisodesUseCase,
) : MavericksViewModel<PodcastViewState>(initialState) {

    init {
        viewModelScope.launch {
            loadPodcast()
        }
    }

    private suspend fun loadPodcast() {
        val storeFront = fetchStoreFrontUseCase.getStoreFront().first()
        withState { state ->
            loadPodcastUseCase
                .execute(state.podcast.feedUrl)
                .filterNotNull()
                .distinctUntilChanged()
                .setOnEach { copy(podcast = it) }

            loadPodcastEpisodesUseCase
                .execute(state.podcast.feedUrl)
                .setOnEach { copy(episodes = it) }
        }
    }
}