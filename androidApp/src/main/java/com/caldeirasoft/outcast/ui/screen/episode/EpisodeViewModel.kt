package com.caldeirasoft.outcast.ui.screen.episode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.usecase.FetchPodcastDataUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadEpisodeFromDbUseCase
import com.caldeirasoft.outcast.ui.navigation.getObject
import com.caldeirasoft.outcast.ui.navigation.getObjectNotNull
import com.caldeirasoft.outcast.ui.screen.MviViewModel
import com.caldeirasoft.outcast.ui.screen.MvieViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadEpisodeFromDbUseCase: LoadEpisodeFromDbUseCase,
    private val fetchPodcastDataUseCase: FetchPodcastDataUseCase,
) : MvieViewModel<EpisodeViewState, EpisodeEvent, EpisodeActions>(
    // The string "episode" is the name of the argument in the route
    EpisodeViewState(
        episode = savedStateHandle.getObjectNotNull("episode"),
        podcast = savedStateHandle.getObject("podcast"))
) {

    private var isInitialized: Boolean = false

    init {
        val initialState = state.value
        viewModelScope.launch {
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
    }

    // get episode data
    @OptIn(FlowPreview::class)
    suspend fun getEpisodeInfo() {

    }

    override suspend fun performAction(action: EpisodeActions) = when(action) {
        is EpisodeActions.OpenPodcastDetail -> openPodcastDetails()
        else -> Unit
    }

    private suspend fun openPodcastDetails() {
        withState {
            it.podcast?.let { podcast ->
                emitEvent(EpisodeEvent.OpenPodcastDetail(podcast))
            }
        }
    }

    data class State(
        val episode: Episode? = null,
        val podcast: Podcast? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null,
    )
    {
        val artistData: StoreData? =
            podcast?.artistUrl?.let {
                StoreData(
                    id = podcast.artistId ?: 0L,
                    label = podcast.artistName,
                    url = podcast.artistUrl.orEmpty(),
                    storeFront = ""
                )
            }
    }
}