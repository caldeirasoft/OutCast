package com.caldeirasoft.outcast.ui.screen.episodelist

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.db.entities.Download
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.caldeirasoft.outcast.data.repository.EpisodesRepository
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.ui.screen.base.BaseViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class EpisodeListViewModel<State: Any, Event: EpisodeListViewModel.Event, Action: EpisodeListViewModel.Action>(
    initialState: State,
    private val episodesRepository: EpisodesRepository,
    private val downloadRepository: DownloadRepository,
) : BaseViewModel<State, EpisodeListViewModel.Event, Action>(initialState)
{
    @OptIn(FlowPreview::class)
    abstract val episodes: Flow<PagingData<EpisodeUiModel>>
    protected val downloadsFlow: MutableStateFlow<List<Download>> =
        MutableStateFlow(emptyList())

    init {
        downloadRepository.getAllDownloads()
            .distinctUntilChanged()
            .onEach { downloads ->
                downloadsFlow.emit(downloads)
            }
            .launchIn(viewModelScope)
    }

    override suspend fun performAction(action: Action) = when (action) {
        is EpisodeListViewModel.Action.OpenPodcastDetail ->
            emitEvent(EpisodeListViewModel.Event.OpenPodcastDetail(action.episode))
        is EpisodeListViewModel.Action.OpenEpisodeDetail ->
            emitEvent(EpisodeListViewModel.Event.OpenEpisodeDetail(action.episode))
        is EpisodeListViewModel.Action.OpenEpisodeContextMenu ->
            emitEvent(EpisodeListViewModel.Event.OpenEpisodeContextMenu(action.episode))
        is EpisodeListViewModel.Action.PlayEpisode -> playEpisode(action.episode)
        is EpisodeListViewModel.Action.PauseEpisode -> playEpisode(action.episode)
        is EpisodeListViewModel.Action.PlayNextEpisode -> playNext(action.episode)
        is EpisodeListViewModel.Action.PlayLastEpisode -> playLast(action.episode)
        is EpisodeListViewModel.Action.ToggleSaveEpisode -> toggleSaveEpisode(action.episode)
        is EpisodeListViewModel.Action.ShareEpisode -> shareEpisode()
        is EpisodeListViewModel.Action.Exit -> emitEvent(EpisodeListViewModel.Event.Exit)
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
            emitEvent(EpisodeListViewModel.Event.ShareEpisode)
        }
    }

    data class State(
        val error: Throwable? = null,
        val episodes: List<Episode> = emptyList(),
        val category: Category? = null,
        val categories: List<Category> = emptyList(),
        val downloads: List<Download> = emptyList(),
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
        data class FilterCategory(val category: Category?): Action()
        object Exit : Action()
    }
}