package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import androidx.datastore.preferences.core.Preferences
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MavericksViewModelFactory
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

class StorePodcastViewModel @AssistedInject constructor(
    @Assisted val initialState: StorePodcastViewState,
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    private val fetchStorePodcastDataUseCase: FetchStorePodcastDataUseCase,
    private val loadPodcastUseCase: LoadPodcastUseCase,
    private val loadFollowedPodcastsUseCase: LoadFollowedPodcastsUseCase,
    private val subscribeUseCase: SubscribeUseCase,
    private val unsubscribeUseCase: UnsubscribeUseCase,
    private val loadPodcastEpisodesUseCase: LoadPodcastEpisodesUseCase,
    private val loadSettingsUseCase: LoadSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
) : MavericksViewModel<StorePodcastViewState>(initialState), PreferenceViewModel {

    @OptIn(FlowPreview::class)
    val episodes: Flow<PagingData<Episode>> =
        loadPodcastEpisodesUseCase.execute(initialState.podcast.feedUrl)
            .map { it.sortedByDescending { it.releaseDateTime } }
            .map { PagingData.from(it) }
            .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            fetchPodcast()
        }

        loadSettingsUseCase.settings
            .setOnEach {
                copy(prefs = it)
            }
    }

    private suspend fun fetchPodcast() {
        val storeFront = fetchStoreFrontUseCase.getStoreFront().first()

        setState {
            copy(storeFront = storeFront)
        }

        // fetch local podcast data subscription
        fetchStorePodcastDataUseCase
            .execute(podcast = initialState.podcast, storeFront = storeFront)
            .filterNotNull()
            .setOnEach {
                when (it) {
                    is Resource.Loading ->
                        copy(isLoading = true)
                    is Resource.Success ->
                        copy(
                            isLoading = false,
                            podcast = it.data,
                            followingStatus =
                            if (it.data.isSubscribed) FollowStatus.FOLLOWED else FollowStatus.UNFOLLOWED,
                            showAllEpisodes = it.data.isSubscribed
                        )
                    else ->
                        copy()
                }
            }
    }

    fun showAllEpisodes() {
        setState {
            copy(showAllEpisodes = true)
        }
    }

    fun subscribe() {
        withState { state ->
            subscribeUseCase.execute(state.podcast.feedUrl)
                .onStart {
                    setState { copy(followingStatus = FollowStatus.FOLLOWING) }
                }
                .launchIn(viewModelScope)
        }
    }

    fun unfollow() {
        viewModelScope.launch {
            unsubscribeUseCase.execute(initialState.podcast.feedUrl)
        }
    }

    override fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            updateSettingsUseCase.updatePreference(key, value)
        }
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<StorePodcastViewModel, StorePodcastViewState> {
        override fun create(initialState: StorePodcastViewState): StorePodcastViewModel
    }

    companion object :
        MavericksViewModelFactory<StorePodcastViewModel, StorePodcastViewState> by hiltMavericksViewModelFactory()
}