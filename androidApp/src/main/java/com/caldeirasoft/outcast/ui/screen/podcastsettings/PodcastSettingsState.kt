package com.caldeirasoft.outcast.ui.screen.podcastsettings

import com.caldeirasoft.outcast.data.common.PodcastPreferences
import com.caldeirasoft.outcast.data.db.entities.Podcast

data class PodcastSettingsState(
    val podcast: Podcast,
    val prefs: PodcastPreferences? = null,
)