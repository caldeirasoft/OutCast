package com.caldeirasoft.outcast.ui.screen.episodes.base

import com.caldeirasoft.outcast.data.db.entities.Episode

sealed class EpisodesEvent {
    data class OpenEpisodeDetail(val episode: Episode) : EpisodesEvent()
    data class OpenEpisodeContextMenu(val episode: Episode) : EpisodesEvent()
    object RefreshList : EpisodesEvent()
}