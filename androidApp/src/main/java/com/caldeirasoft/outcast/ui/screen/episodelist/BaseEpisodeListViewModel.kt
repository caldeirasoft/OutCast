package com.caldeirasoft.outcast.ui.screen.episodelist

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.caldeirasoft.outcast.data.db.entities.Download
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.PodcastWithCount
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.caldeirasoft.outcast.data.repository.EpisodesRepository
import com.caldeirasoft.outcast.ui.screen.base.BaseViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseEpisodeListViewModel<State: Any, Event: BaseEpisodeListViewModel.Event, Action: BaseEpisodeListViewModel.Action>(
    initialState: State,
    protected val episodesRepository: EpisodesRepository,
    private val downloadRepository: DownloadRepository,
) : BaseViewModel<State, BaseEpisodeListViewModel.Event, Action>(initialState)
{
    protected val downloadsFlow: MutableStateFlow<List<Download>> =
        MutableStateFlow(emptyList())

    override fun activate() {
        downloadRepository.getAllDownloads()
            .distinctUntilChanged()
            .onEach { downloads ->
                downloadsFlow.emit(downloads)
            }
            .launchIn(viewModelScope)
    }

    protected fun getEpisodesPagingData(): Flow<PagingData<Episode>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                maxSize = 4000,
                prefetchDistance = 5
            ),
            initialKey = null,
            pagingSourceFactory = getEpisodesDataSource().asPagingSourceFactory()
        ).flow

    abstract fun getEpisodes(): Flow<PagingData<EpisodeUiModel>>

    protected abstract fun getEpisodesDataSource(): DataSource.Factory<Int, Episode>

    override suspend fun performAction(action: Action) = when (action) {
        is BaseEpisodeListViewModel.Action.OpenPodcastDetail ->
            emitEvent(BaseEpisodeListViewModel.Event.OpenPodcastDetail(action.episode))
        is BaseEpisodeListViewModel.Action.OpenEpisodeDetail ->
            emitEvent(BaseEpisodeListViewModel.Event.OpenEpisodeDetail(action.episode))
        is BaseEpisodeListViewModel.Action.OpenEpisodeContextMenu ->
            emitEvent(BaseEpisodeListViewModel.Event.OpenEpisodeContextMenu(action.episode))
        is BaseEpisodeListViewModel.Action.PlayEpisode -> playEpisode(action.episode)
        is BaseEpisodeListViewModel.Action.PauseEpisode -> playEpisode(action.episode)
        is BaseEpisodeListViewModel.Action.PlayNextEpisode -> playNext(action.episode)
        is BaseEpisodeListViewModel.Action.PlayLastEpisode -> playLast(action.episode)
        is BaseEpisodeListViewModel.Action.ToggleSaveEpisode -> toggleSaveEpisode(action.episode)
        is BaseEpisodeListViewModel.Action.ShareEpisode -> shareEpisode()
        is BaseEpisodeListViewModel.Action.Exit -> emitEvent(BaseEpisodeListViewModel.Event.Exit)
        else -> Unit
    }

    protected fun playEpisode(episode: Episode) {
    }

    protected fun playNext(episode: Episode) {
    }

    protected fun playLast(episode: Episode) {
    }

    protected fun toggleSaveEpisode(episode: Episode) {
        viewModelScope.withState {
            if (episode.isSaved.not()) episodesRepository.saveEpisodeToLibrary(episode)
            else episodesRepository.deleteFromLibrary(episode)
        }
    }

    protected fun shareEpisode() {
        viewModelScope.launch {
            emitEvent(BaseEpisodeListViewModel.Event.ShareEpisode)
        }
    }

    data class State(
        val error: Throwable? = null,
        val episodes: List<Episode> = emptyList(),
        val downloads: List<Download> = emptyList(),
        val podcastsWithCount: List<PodcastWithCount> = emptyList(),
        val podcastFilter: String? = null,
    )

    open class Event {
        data class OpenPodcastDetail(val episode: Episode) : Event()
        data class OpenEpisodeDetail(val episode: Episode) : Event()
        data class OpenEpisodeContextMenu(val episode: Episode) : Event()
        object RefreshList : Event()
        object ShareEpisode : Event()
        object Exit : Event()
    }

    open class Action {
        data class OpenPodcastDetail(val episode: Episode) : Action()
        data class OpenEpisodeDetail(val episode: Episode) : Action()
        data class OpenEpisodeContextMenu(val episode: Episode) : Action()
        data class PlayEpisode(val episode: Episode) : Action()
        data class PauseEpisode(val episode: Episode) : Action()
        data class PlayNextEpisode(val episode: Episode) : Action()
        data class PlayLastEpisode(val episode: Episode) : Action()
        data class ToggleSaveEpisode(val episode: Episode) : Action()
        data class ShareEpisode(val episode: Episode) : Action()
        data class FilterByPodcast(val feedUrl: String?): Action()
        object Exit : Action()
    }
}