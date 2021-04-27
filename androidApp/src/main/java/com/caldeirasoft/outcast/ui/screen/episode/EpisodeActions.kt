package com.caldeirasoft.outcast.ui.screen.episode

import com.caldeirasoft.outcast.data.db.entities.Podcast

sealed class EpisodeActions {
    object OpenPodcastDetail : EpisodeActions()
    object NavigateUp : EpisodeActions()
}