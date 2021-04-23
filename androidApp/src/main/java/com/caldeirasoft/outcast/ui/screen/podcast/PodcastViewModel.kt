package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.datastore.preferences.core.Preferences
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.di.hiltmavericks.AssistedViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.hiltMavericksViewModelFactory
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.components.preferences.PreferenceViewModel
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class PodcastViewModel @AssistedInject constructor(
    @Assisted val initialState: PodcastState,
    private val fetchPodcastDataUseCase: FetchPodcastDataUseCase,
    private val updatePodcastDataUseCase: UpdatePodcastDataUseCase,
    private val followUseCase: FollowUseCase,
    private val unfollowUseCase: UnfollowUseCase,
    private val loadSettingsUseCase: LoadSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val podcastDao: PodcastDao,
) : MavericksViewModel<PodcastState>(initialState), PreferenceViewModel {

    private var isInitialized: Boolean = false

    init {
        podcastDao.getPodcastAndEpisodesWithUrl(initialState.podcast.feedUrl)
            .onEach {
                if (it == null) // 1rst launch
                    fetchPodcastDataUseCase.execute(initialState.podcast)
                        .onStart { setState { copy(isLoading = true) } }
                        .launchIn(viewModelScope)
                else if (!isInitialized) // podcast in db : update if necessary
                    updatePodcastDataUseCase.execute(it.podcast)
                        .launchIn(viewModelScope)
                isInitialized = true
            }
            .filterNotNull()
            .setOnEach {
                copy(
                    isLoading = false,
                    podcast = it.podcast,
                    followingStatus = if (it.podcast.isFollowed) FollowStatus.FOLLOWED else FollowStatus.UNFOLLOWED,
                    episodes = it.episodes.sortedByDescending { episode ->  episode.releaseDateTime },
                    showAllEpisodes = it.podcast.isFollowed
                )
            }


        loadSettingsUseCase.settings
            .setOnEach {
                copy(prefs = it)
            }
    }

    fun showAllEpisodes() {
        setState {
            copy(showAllEpisodes = true)
        }
    }

    fun subscribe() {
        followUseCase.execute(initialState.podcast.feedUrl)
            .onStart { setState { copy(followingStatus = FollowStatus.FOLLOWING) } }
            .launchIn(viewModelScope)
    }

    fun unfollow() {
        viewModelScope.launch {
            unfollowUseCase.execute(feedUrl = initialState.podcast.feedUrl)
        }
    }

    override fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            updateSettingsUseCase.updatePreference(key, value)
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<PodcastViewModel, PodcastState> {
        override fun create(initialState: PodcastState): PodcastViewModel
    }

    companion object :
        MavericksViewModelFactory<PodcastViewModel, PodcastState> by hiltMavericksViewModelFactory()
}