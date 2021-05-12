package com.caldeirasoft.outcast.ui.screen.episodes.base

import com.caldeirasoft.outcast.data.db.entities.Episode
import kotlinx.datetime.Instant

sealed class EpisodesUiModel {
    data class EpisodeItem(val episode: Episode) : EpisodesUiModel()
    data class SeparatorItem(val date: Instant) : EpisodesUiModel()
}