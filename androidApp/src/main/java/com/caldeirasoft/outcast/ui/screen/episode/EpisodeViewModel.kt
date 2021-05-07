package com.caldeirasoft.outcast.ui.screen.episode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.episode
import com.caldeirasoft.outcast.domain.models.podcast
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.usecase.FetchPodcastDataUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadEpisodeFromDbUseCase
import com.caldeirasoft.outcast.ui.navigation.Screen.Companion.urlDecode
import com.caldeirasoft.outcast.ui.navigation.Screen.Companion.urlEncode
import com.caldeirasoft.outcast.ui.navigation.getObject
import com.caldeirasoft.outcast.ui.navigation.getObjectNotNull
import com.caldeirasoft.outcast.ui.screen.MviViewModel
import com.caldeirasoft.outcast.ui.screen.MvieViewModel
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastActions
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
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
        feedUrl = savedStateHandle.get<String>("feedUrl").orEmpty(),
        guid = savedStateHandle.get<String>("guid")?.urlDecode().orEmpty(),
    )
) {

    private var isInitialized: Boolean = false
    private var isEpisodeSet: Boolean = false

    init {
        val initialState = state.value
        viewModelScope.launch {
            loadEpisodeFromDbUseCase
                .execute(
                    feedUrl = initialState.feedUrl,
                    guid = initialState.guid
                )
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
        is EpisodeActions.SetEpisode -> setEpisode(action.storeEpisode)
        is EpisodeActions.OpenPodcastDetail -> openPodcastDetails()
        else -> Unit
    }

    private fun setEpisode(storeEpisode: StoreEpisode) {
        if (!isEpisodeSet) {
            fetchPodcastDataUseCase
                .execute(storeEpisode.storePodcast.podcast)
                .onStart {
                    isEpisodeSet = true
                    setState {
                        copy(
                            isLoading = true,
                            episode = storeEpisode.episode,
                            podcast = storeEpisode.storePodcast.podcast
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private suspend fun openPodcastDetails() {
        withState {
            it.podcast?.let { podcast ->
                emitEvent(EpisodeEvent.OpenPodcastDetail(podcast))
            }
        }
    }
}