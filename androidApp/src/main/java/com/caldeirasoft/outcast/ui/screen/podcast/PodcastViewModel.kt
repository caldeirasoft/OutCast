package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.*
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast.Companion.podcastFilterOption
import com.caldeirasoft.outcast.data.db.entities.Podcast.Companion.podcastSortOrderOption
import com.caldeirasoft.outcast.data.repository.*
import com.caldeirasoft.outcast.domain.enums.PodcastFilter
import com.caldeirasoft.outcast.domain.enums.SortOrder
import com.caldeirasoft.outcast.domain.models.podcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.usecase.*
import com.caldeirasoft.outcast.ui.components.preferences.PreferenceViewModel
import com.caldeirasoft.outcast.ui.screen.episodes.EpisodeListViewModel
import com.caldeirasoft.outcast.ui.screen.base.EpisodeUiModel
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import com.caldeirasoft.outcast.ui.screen.store.storedata.args.PodcastRouteArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val podcastsRepository: PodcastsRepository,
    private val settingsRepository: SettingsRepository,
    saveEpisodeUseCase: SaveEpisodeUseCase,
    removeSaveEpisodeUseCase: RemoveSaveEpisodeUseCase,
    downloadRepository: DownloadRepository,
    episodesRepository: EpisodesRepository,
) : EpisodeListViewModel<PodcastState, PodcastEvent>(
    initialState = PodcastState(
        feedUrl = PodcastRouteArgs.fromSavedStatedHandle(savedStateHandle).feedUrl,
        isLoading = true,
    ),
    saveEpisodeUseCase = saveEpisodeUseCase,
    removeSaveEpisodeUseCase = removeSaveEpisodeUseCase,
    downloadRepository = downloadRepository
), PreferenceViewModel {

    private var isInitialized: Boolean = false
    private var isPodcastSet: Boolean = false

    @OptIn(FlowPreview::class)
    override val episodes: Flow<PagingData<EpisodeUiModel>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                maxSize = 4000,
                prefetchDistance = 5
            ),
            initialKey = null,
            pagingSourceFactory = episodesRepository.getEpisodesDataSourceWithUrl(initialState.feedUrl).asPagingSourceFactory()
        )
            .flow
            .map { pagingData -> pagingData.map { EpisodeUiModel.EpisodeItem(it) as EpisodeUiModel }}
            .cachedIn(viewModelScope)


    init {
        podcastsRepository
            .loadPodcast(initialState.feedUrl)
            .onEach {
                // 1rst launch
                if ((it != null) && (!isInitialized)) {
                    // podcast in db : update if necessary
                    podcastsRepository
                        .updatePodcastItunesMetadata(it)
                }
                isInitialized = true
            }
            .filterNotNull()
            .setOnEach {
                copy(
                    isLoading = false,
                    podcast = it,
                    followingStatus = if (it.isFollowed) FollowStatus.FOLLOWED else FollowStatus.UNFOLLOWED,
                    showAllEpisodes = it.isFollowed,
                    sortOrder = it.podcastSortOrderOption,
                    filter = it.podcastFilterOption
                )
            }
    }

    fun setPodcast(storePodcast: StorePodcast) {
        if (!isPodcastSet) {
            viewModelScope.launch {
                setState {
                    copy(
                        isLoading = true,
                        podcast = storePodcast.podcast
                    )
                }
                podcastsRepository
                    .updatePodcastItunesMetadata(storePodcast.podcast)
                isPodcastSet = true
            }
        }
    }


    suspend fun openEpisodeDetails(episode: Episode) {
        withState {
            emitEvent(PodcastEvent.OpenEpisodeDetail(episode))
        }
    }

    fun follow() {
        viewModelScope.launch {
            setState { copy(followingStatus = FollowStatus.FOLLOWING) }
            podcastsRepository.followPodcast(feedUrl = initialState.feedUrl)
        }
    }

    fun unfollow() {
        viewModelScope.launch {
            podcastsRepository.unfollowPodcast(feedUrl = initialState.feedUrl)
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
            podcastsRepository.updatePodcastSortOrder(it.feedUrl, if (it.sortOrder == SortOrder.DESC) SortOrder.ASC else SortOrder.DESC)
        }
    }

    fun updateSortOrder(sortOrder: SortOrder) {
        viewModelScope.withState {
            podcastsRepository.updatePodcastSortOrder(it.feedUrl, sortOrder)
        }
    }

    fun updateFilter(filter: PodcastFilter) {
        viewModelScope.withState {
            podcastsRepository.updatePodcastFilter(it.feedUrl, filter)
        }
    }

    override fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        viewModelScope.launch {
            //updateSettingsUseCase.updatePreference(key, value)
        }
    }
}