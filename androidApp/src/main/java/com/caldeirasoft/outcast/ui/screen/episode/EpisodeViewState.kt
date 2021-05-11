package com.caldeirasoft.outcast.ui.screen.episode

import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast

data class EpisodeViewState(
    val feedUrl: String,
    val guid: String,
    val episode: Episode? = null,
    val podcast: Podcast? = null,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
)

sealed class EpisodeEvent {
    data class OpenPodcastDetail(val podcast: Podcast) : EpisodeEvent()
}