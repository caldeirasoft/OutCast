package com.caldeirasoft.outcast.ui.screen.episode

import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.AssistedViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.hiltMavericksViewModelFactory
import com.caldeirasoft.outcast.domain.usecase.FetchPodcastDataUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadEpisodeFromDbUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class EpisodeViewModel @AssistedInject constructor(
    @Assisted initialState: EpisodeViewState,
    private val loadEpisodeFromDbUseCase: LoadEpisodeFromDbUseCase,
    private val fetchPodcastDataUseCase: FetchPodcastDataUseCase,
) : MavericksViewModel<EpisodeViewState>(initialState) {

    private var isInitialized: Boolean = false

    init {
        loadEpisodeFromDbUseCase
            .execute(
                feedUrl = initialState.episode.feedUrl,
                guid = initialState.episode.guid
            )
            .onEach {
                if (it == null && !isInitialized) {
                    // 1rst launch
                    initialState.podcast?.let { podcast ->
                        fetchPodcastDataUseCase
                            .execute(podcast)
                            .onStart { setState { copy(isLoading = true) } }
                            .launchIn(viewModelScope)
                    }
                }
                isInitialized = true
            }
            .filterNotNull()
            .setOnEach {
                copy(
                    isLoading = false,
                    episode = it.episode,
                    podcast = it.podcast
                )
            }
    }

    // get episode data
    @OptIn(FlowPreview::class)
    suspend fun getEpisodeInfo() {

    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<EpisodeViewModel, EpisodeViewState> {
        override fun create(initialState: EpisodeViewState): EpisodeViewModel
    }

    companion object :
        MavericksViewModelFactory<EpisodeViewModel, EpisodeViewState> by hiltMavericksViewModelFactory()
}