package com.caldeirasoft.outcast.ui.screen.episodes

import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeEvent

sealed class EpisodesEvent {
    data class OpenEpisodeDetail(val episode: Episode) : EpisodesEvent()
    data class OpenEpisodeContextMenu(val episode: Episode) : EpisodesEvent()
    object RefreshList : EpisodesEvent()

    object PlayEpisodeEvent : EpisodesEvent()
    object PauseEpisodeEvent : EpisodesEvent()
    object PlayNextEpisodeEvent : EpisodesEvent()
    object PlayLastEpisodeEvent : EpisodesEvent()
    object DownloadEpisodeEvent : EpisodesEvent()
    object CancelDownloadEpisodeEvent : EpisodesEvent()
    object RemoveDownloadEpisodeEvent : EpisodesEvent()
    object SaveEpisodeEvent : EpisodesEvent()
    object RemoveFromSavedEpisodesEvent : EpisodesEvent()
    object ShareEpisodeEvent : EpisodesEvent()
}