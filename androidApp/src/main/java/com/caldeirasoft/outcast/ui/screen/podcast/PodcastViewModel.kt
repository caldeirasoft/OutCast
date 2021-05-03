package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.ui.components.preferences.PreferenceViewModel
import com.caldeirasoft.outcast.ui.navigation.getObjectNotNull
import com.caldeirasoft.outcast.ui.screen.MvieViewModel
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PodcastViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadPodcastFromDbUseCase: LoadPodcastFromDbUseCase,
    private val loadPodcastEpisodesPagingDataUseCase: LoadPodcastEpisodesPagingDataUseCase,
    private val fetchPodcastDataUseCase: FetchPodcastDataUseCase,
    private val updatePodcastDataUseCase: UpdatePodcastDataUseCase,
    private val followUseCase: FollowUseCase,
    private val unfollowUseCase: UnfollowUseCase,
    private val loadSettingsUseCase: LoadSettingsUseCase,
) : MvieViewModel<PodcastState, PodcastEvent, PodcastActions>(
    initialState = PodcastState(
        podcast = savedStateHandle.getObjectNotNull("podcast"),
        isLoading = true
    )
), PreferenceViewModel {

    private var isInitialized: Boolean = false

    val dataStore = loadSettingsUseCase.dataStore

    @OptIn(FlowPreview::class)
    val episodes: Flow<PagingData<Episode>> =
        loadPodcastEpisodesPagingDataUseCase.execute(initialState.podcast)
            .onEach { Timber.d("loadPodcastEpisodesUseCase : ${it} episodes") }
            .cachedIn(viewModelScope)

    init {
        loadPodcastFromDbUseCase.execute(initialState.podcast)
            .onEach {
                if (it == null) // 1rst launch
                    fetchPodcastDataUseCase.execute(initialState.podcast)
                        .onStart { setState { copy(isLoading = true) } }
                        .launchIn(viewModelScope)
                else if (!isInitialized) // podcast in db : update if necessary
                    updatePodcastDataUseCase.execute(it)
                        .launchIn(viewModelScope)
                isInitialized = true
            }
            .filterNotNull()
            .setOnEach {
                copy(
                    isLoading = false,
                    podcast = it,
                    followingStatus = if (it.isFollowed) FollowStatus.FOLLOWED else FollowStatus.UNFOLLOWED,
                    showAllEpisodes = it.isFollowed
                )
            }


        loadSettingsUseCase.settings
            .setOnEach {
                copy(prefs = it)
            }
    }

    override suspend fun performAction(action: PodcastActions) = when(action) {
        is PodcastActions.OpenEpisodeDetail -> openEpisodeDetails(action.episode)
        is PodcastActions.FollowPodcast -> follow()
        is PodcastActions.UnfollowPodcast -> unfollow()
        is PodcastActions.ShowAllEpisodes -> showAllEpisodes()
        else -> Unit
    }

    private suspend fun openEpisodeDetails(episode: Episode) {
        withState {
            it.podcast.let { podcast ->
                emitEvent(PodcastEvent.OpenEpisodeDetail(episode))
            }
        }
    }

    private fun showAllEpisodes() {
        viewModelScope.setState {
            copy(showAllEpisodes = true)
        }
    }

    private fun follow() {
        followUseCase.execute(initialState.podcast.feedUrl)
            .onStart { setState { copy(followingStatus = FollowStatus.FOLLOWING) } }
            .launchIn(viewModelScope)
    }

    private fun unfollow() {
        viewModelScope.launch {
            unfollowUseCase.execute(feedUrl = initialState.podcast.feedUrl)
        }
    }

    override fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            //updateSettingsUseCase.updatePreference(key, value)
        }
    }
}