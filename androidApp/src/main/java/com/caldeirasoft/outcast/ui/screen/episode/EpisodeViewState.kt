package com.caldeirasoft.outcast.ui.screen.episode

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.db.Episode

data class EpisodeViewState(
    val episode: Episode,
    val isLoading: Boolean = false,
) : MavericksState
{
    constructor(episodeArg: EpisodeArg) : this(episode = episodeArg.toEpisode())
}