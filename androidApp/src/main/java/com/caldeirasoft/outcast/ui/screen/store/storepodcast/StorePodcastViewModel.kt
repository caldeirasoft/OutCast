package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import androidx.datastore.preferences.core.Preferences
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.ui.components.preferences.PreferenceViewModel
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
class StorePodcastViewModel(
    val initialState: StorePodcastViewState,
) : MavericksViewModel<StorePodcastViewState>(initialState), KoinComponent, PreferenceViewModel {
    private val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()
    private val fetchStorePodcastDataUseCase: FetchStorePodcastDataUseCase by inject()
    private val loadPodcastUseCase: LoadPodcastUseCase by inject()
    private val loadFollowedPodcastsUseCase: LoadFollowedPodcastsUseCase by inject()
    private val subscribeUseCase: SubscribeUseCase by inject()
    private val unsubscribeUseCase: UnsubscribeUseCase by inject()
    private val loadPodcastEpisodesUseCase: LoadPodcastEpisodesUseCase by inject()
    private val loadSettingsUseCase: LoadSettingsUseCase by inject()
    private val updateSettingsUseCase: UpdateSettingsUseCase by inject()

    @OptIn(FlowPreview::class)
    val episodes: Flow<PagingData<Episode>> =
        loadPodcastEpisodesUseCase.execute(initialState.podcast.podcastId)
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
            subscribeUseCase.execute(state.podcast.podcastId, NewEpisodesAction.INBOX)
                .onStart {
                    setState { copy(followingStatus = FollowStatus.FOLLOWING) }
                }
                .launchIn(viewModelScope)
        }
    }

    fun unfollow() {
        viewModelScope.launch {
            unsubscribeUseCase.execute(initialState.podcast.podcastId)
        }
    }

    override fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            updateSettingsUseCase.updatePreference(key, value)
        }
    }
}