package com.caldeirasoft.outcast.ui.screen.episodes

import com.caldeirasoft.outcast.data.db.entities.Episode
import kotlinx.datetime.Instant

sealed class EpisodeUiModel {
    data class EpisodeItem(val episode: Episode) : EpisodeUiModel()
    data class SeparatorItem(val date: Instant) : EpisodeUiModel()
}