package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.db.entities.Podcast.Companion.podcastFilterOption
import com.caldeirasoft.outcast.data.db.entities.Podcast.Companion.podcastSortOrderOption
import com.caldeirasoft.outcast.data.db.entities.PodcastSettings
import com.caldeirasoft.outcast.data.repository.*
import com.caldeirasoft.outcast.domain.enums.PodcastFilter
import com.caldeirasoft.outcast.domain.enums.SortOrder
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.podcast
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.components.preferences.PreferenceViewModel
import com.caldeirasoft.outcast.ui.screen.episodelist.BaseEpisodeListViewModel
import com.caldeirasoft.outcast.ui.screen.episodelist.EpisodeUiModel
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
    downloadRepository: DownloadRepository,
    episodesRepository: EpisodesRepository,
) : BaseEpisodeListViewModel<PodcastViewModel.State, PodcastViewModel.Event, PodcastViewModel.Action>(
    initialState = State(
        feedUrl = PodcastRouteArgs.fromSavedStatedHandle(savedStateHandle).feedUrl,
        isLoading = true,
    ),
    episodesRepository = episodesRepository,
    downloadRepository = downloadRepository
), PreferenceViewModel {

    private var isInitialized: Boolean = false
    private var isPodcastSet: Boolean = false

    override fun activate() {
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

    override suspend fun performAction(action: Action) = when (action){
        is Action.SetPodcast -> setPodcast(action.podcast)
        is Action.Follow -> follow()
        is Action.Unfollow -> unfollow()
        is Action.OpenWebsite -> openPodcastWebsite()
        is Action.SharePodcast -> sharePodcast()
        is Action.OpenStoreData -> Unit
        is Action.ToggleSortOrder -> togglePodcastSortOrder()
        is Action.FilterEpisodes -> updateFilter(action.filter)
        is Action.OpenPodcastDetail ->
            emitEvent(Event.OpenPodcastDetail(action.episode))
        is Action.OpenEpisodeDetail ->
            emitEvent(Event.OpenEpisodeDetail(action.episode))
        is Action.OpenEpisodeContextMenu ->
            emitEvent(Event.OpenEpisodeContextMenu(action.episode))
        is Action.OpenSettings -> emitEvent(Event.OpenSettings(initialState.feedUrl))
        is Action.ToggleNotifications -> Unit
        is Action.FilterCategory -> Unit
        is Action.PlayEpisode -> playEpisode(action.episode)
        is Action.PauseEpisode -> playEpisode(action.episode)
        is Action.PlayNextEpisode -> playNext(action.episode)
        is Action.PlayLastEpisode -> playLast(action.episode)
        is Action.ToggleSaveEpisode -> toggleSaveEpisode(action.episode)
        is Action.ShareEpisode -> shareEpisode()
        is Action.Exit -> emitEvent(Event.Exit)
    }

    @OptIn(FlowPreview::class)
    override fun getEpisodes(): Flow<PagingData<EpisodeUiModel>> =
        getEpisodesPagingData()
            .map { pagingData -> pagingData.map { EpisodeUiModel.EpisodeItem(it) as EpisodeUiModel }}
            .cachedIn(viewModelScope)

    override fun getEpisodesDataSource(): DataSource.Factory<Int, Episode> =
        episodesRepository.getEpisodesDataSourceWithUrl(initialState.feedUrl)

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

    private fun follow() {
        viewModelScope.launch {
            setState { copy(followingStatus = FollowStatus.FOLLOWING) }
            podcastsRepository.followPodcast(feedUrl = initialState.feedUrl)
        }
    }

    private fun unfollow() {
        viewModelScope.launch {
            podcastsRepository.unfollowPodcast(feedUrl = initialState.feedUrl)
        }
    }

    fun toggleNotifications() {
        viewModelScope.withState {
            it.podcast?.let { podcast ->
                emitEvent(Event.ToggleNotifications)
            }
        }
    }

    private fun sharePodcast() {
        viewModelScope.withState {
            it.podcast?.let { podcast ->
                emitEvent(Event.SharePodcast(podcast))
            }
        }
    }

    fun openPodcastWebsite() {
        viewModelScope.withState {
            it.podcast?.podcastWebsiteURL?.let { url ->
                emitEvent(Event.OpenWebsite(url))
            }
        }
    }

    fun togglePodcastSortOrder() {
        viewModelScope.withState {
            podcastsRepository.updatePodcastSortOrder(
                it.feedUrl,
                if (it.sortOrder == SortOrder.DESC) SortOrder.ASC else SortOrder.DESC)
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

    data class State(
        val feedUrl: String,
        val podcast: Podcast? = null,
        val isLoading: Boolean = false,
        val error: Throwable? = null,
        val storeFront: String? = null,
        val episodes: List<Episode> = emptyList(),
        val showAllEpisodes: Boolean = false,
        val followingStatus: FollowStatus = FollowStatus.UNFOLLOWED,
        val podcastSettings: PodcastSettings? = null,
        val sortOrder: SortOrder? = null,
        val filter: PodcastFilter = PodcastFilter.ALL
    ) {
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

    sealed class Event : BaseEpisodeListViewModel.Event() {
        data class OpenPodcastDetail(val episode: Episode) : Event()
        data class OpenEpisodeDetail(val episode: Episode) : Event()
        data class OpenEpisodeContextMenu(val episode: Episode) : Event()
        data class OpenStoreData(val storeData: StoreData) : Event()
        data class OpenSettings(val feedUrl: String) : Event()
        data class SharePodcast(val podcast: Podcast) : Event()
        data class OpenWebsite(val websiteUrl: String) : Event()
        object ToggleNotifications : Event()
        object Exit : Event()
    }

    sealed class Action : BaseEpisodeListViewModel.Action() {
        data class SetPodcast(val podcast: StorePodcast) : Action()
        data class OpenStoreData(val storeData: StoreData) : Action()
        data class OpenPodcastDetail(val episode: Episode) : Action()
        data class OpenEpisodeDetail(val episode: Episode) : Action()
        data class OpenEpisodeContextMenu(val episode: Episode) : Action()
        data class PlayEpisode(val episode: Episode) : Action()
        data class PauseEpisode(val episode: Episode) : Action()
        data class PlayNextEpisode(val episode: Episode) : Action()
        data class PlayLastEpisode(val episode: Episode) : Action()
        data class ToggleSaveEpisode(val episode: Episode) : Action()
        data class ShareEpisode(val episode: Episode) : Action()
        data class FilterCategory(val category: Category?): Action()
        object OpenSettings : Action()
        object SharePodcast : Action()
        object OpenWebsite : Action()
        object ToggleSortOrder : Action()
        object Follow : Action()
        object Unfollow : Action()
        data class FilterEpisodes(val filter: PodcastFilter) : Action()
        object ToggleNotifications : Action()
        object Exit : Action()
    }
}