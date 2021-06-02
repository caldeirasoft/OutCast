package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import com.caldeirasoft.outcast.domain.enums.PodcastFilter
import com.caldeirasoft.outcast.domain.enums.SortOrder
import com.caldeirasoft.outcast.domain.models.podcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.ui.components.preferences.PreferenceViewModel
import com.caldeirasoft.outcast.ui.screen.episodes.EpisodeListViewModel
import com.caldeirasoft.outcast.ui.screen.episodes.EpisodeUiModel
import com.caldeirasoft.outcast.ui.screen.episodes.EpisodesEvent
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadPodcastFromDbUseCase: LoadPodcastFromDbUseCase,
    private val loadPodcastEpisodesPagingDataUseCase: LoadPodcastEpisodesPagingDataUseCase,
    private val fetchPodcastDataUseCase: FetchPodcastDataUseCase,
    private val followUseCase: FollowUseCase,
    private val unfollowUseCase: UnfollowUseCase,
    private val loadSettingsUseCase: LoadSettingsUseCase,
    private val getPodcastSortOrderUseCase: GetPodcastSortOrderUseCase,
    private val updatePodcastSortOrderUseCase: UpdatePodcastSortOrderUseCase,
    private val getPodcastFilterUseCase: GetPodcastFilterUseCase,
    private val updatePodcastFilterUseCase: UpdatePodcastFilterUseCase,
    private val podcastsRepository: PodcastsRepository,
    private val dataStoreRepository: DataStoreRepository,
    saveEpisodeUseCase: SaveEpisodeUseCase,
    removeSaveEpisodeUseCase: RemoveSaveEpisodeUseCase,
    downloadRepository: DownloadRepository,
) : EpisodeListViewModel<PodcastState, PodcastEvent>(
    initialState = PodcastState(
        feedUrl = savedStateHandle.get<String>("feedUrl").orEmpty(),
        isLoading = true
    ),
    saveEpisodeUseCase = saveEpisodeUseCase,
    removeSaveEpisodeUseCase = removeSaveEpisodeUseCase,
    downloadRepository = downloadRepository
), PreferenceViewModel {

    private var isInitialized: Boolean = false
    private var isPodcastSet: Boolean = false

    val dataStore = loadSettingsUseCase.dataStore

    @OptIn(FlowPreview::class)
    override val episodes: Flow<PagingData<EpisodeUiModel>> =
        selectSubscribe(PodcastState::sortOrder)
            .distinctUntilChanged()
            .filterNotNull()
            .flatMapLatest { sortOrder -> loadPodcastEpisodesPagingDataUseCase
                .execute(initialState.feedUrl, sortOrder) }
            .map { pagingData -> pagingData.map { EpisodeUiModel.EpisodeItem(it) as EpisodeUiModel }}
            .cachedIn(viewModelScope)


    init {
        loadPodcastFromDbUseCase.execute(initialState.feedUrl)
            .onEach {
                // 1rst launch
                if ((it != null) && (!isInitialized)) {
                    // podcast in db : update if necessary
                    fetchPodcastDataUseCase
                        .execute(it)
                        .launchIn(viewModelScope)
                }
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

        getPodcastSortOrderUseCase.execute(initialState.feedUrl)
            .setOnEach {
                copy(sortOrder = it)
            }

        getPodcastFilterUseCase.execute(initialState.feedUrl)
            .setOnEach {
                copy(filter = it)
            }
    }

    fun setPodcast(storePodcast: StorePodcast) {
        if (!isPodcastSet) {
            fetchPodcastDataUseCase
                .execute(storePodcast.podcast)
                .onStart {
                    setState {
                        copy(
                            isLoading = true,
                            podcast = storePodcast.podcast
                        )
                    }
                }
                .onCompletion { isPodcastSet = true }
                .launchIn(viewModelScope)
        }
    }


    suspend fun openEpisodeDetails(episode: Episode) {
        withState {
            emitEvent(PodcastEvent.OpenEpisodeDetail(episode))
        }
    }

    fun follow() {
        followUseCase.execute(initialState.feedUrl)
            .onStart { setState { copy(followingStatus = FollowStatus.FOLLOWING) } }
            .launchIn(viewModelScope)
    }

    fun unfollow() {
        viewModelScope.launch {
            unfollowUseCase.execute(feedUrl = initialState.feedUrl)
        }
    }

    fun toggleNotifications() {
        viewModelScope.withState {
            it.podcast?.let { podcast ->
                emitEvent(PodcastEvent.ToggleNotifications)
            }
        }
    }

    fun sharePodcast() {
        viewModelScope.withState {
            it.podcast?.let { podcast ->
                emitEvent(PodcastEvent.SharePodcast(podcast))
            }
        }
    }

    fun openPodcastWebsite() {
        viewModelScope.withState {
            it.podcast?.podcastWebsiteURL?.let { url ->
                emitEvent(PodcastEvent.OpenWebsite(url))
            }
        }
    }

    fun togglePodcastSortOrder() {
        viewModelScope.withState {
            updatePodcastSortOrderUseCase.execute(
                feedUrl = it.feedUrl,
                sortOrder = if (it.sortOrder == SortOrder.DESC) SortOrder.ASC else SortOrder.DESC
            )
        }
    }

    fun updateFilter(filter: PodcastFilter) {
        viewModelScope.withState {
            updatePodcastFilterUseCase.execute(
                feedUrl = it.feedUrl,
                filter = filter
            )
            emitEvent(EpisodesEvent.RefreshList)
        }
    }

    override fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            //updateSettingsUseCase.updatePreference(key, value)
        }
    }
}