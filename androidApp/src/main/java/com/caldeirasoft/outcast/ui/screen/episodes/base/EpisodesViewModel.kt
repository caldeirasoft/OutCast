package com.caldeirasoft.outcast.ui.screen.episodes.base

import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.ui.screen.MvieViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

abstract class EpisodesViewModel(
    initialState: EpisodesState,
) : MvieViewModel<EpisodesState, EpisodesEvent, EpisodesActions>(initialState) {

    @OptIn(FlowPreview::class)
    abstract val episodes: Flow<PagingData<EpisodesUiModel>>

    override suspend fun performAction(action: EpisodesActions) = when(action) {
        is EpisodesActions.OpenEpisodeDetail ->
            openEpisodeDetails(action.episode)
        is EpisodesActions.OpenEpisodeContextMenu ->
            emitEvent(EpisodesEvent.OpenEpisodeContextMenu(action.episode))
        else -> Unit
    }

    private suspend fun openEpisodeDetails(episode: Episode) {
        withState {
            emitEvent(EpisodesEvent.OpenEpisodeDetail(episode))
        }
    }
}