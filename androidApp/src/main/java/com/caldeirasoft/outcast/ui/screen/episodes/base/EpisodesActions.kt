package com.caldeirasoft.outcast.ui.screen.episodes.base

import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.models.Category

sealed class EpisodesActions {
    data class OpenEpisodeDetail(val episode: Episode) : EpisodesActions()
    object NavigateUp : EpisodesActions()
    data class OpenEpisodeContextMenu(val episode: Episode) : EpisodesActions()
    data class PlayNextEpisode(val episode: Episode) : EpisodesActions()
    data class PlayLastEpisode(val episode: Episode) : EpisodesActions()
    data class SaveEpisode(val episode: Episode) : EpisodesActions()
    data class ShareEpisode(val episode: Episode) : EpisodesActions()
    data class FilterByCategory(val category: Category?): EpisodesActions()
}