package com.caldeirasoft.outcast.ui.screen.podcastsettings

import com.caldeirasoft.outcast.data.common.PodcastPreferences
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.db.entities.PodcastSettings
import com.caldeirasoft.outcast.data.db.entities.Settings

data class PodcastSettingsState(
    val feedUrl: String,
    val settings: Settings? = null,
    val podcastSettings: PodcastSettings? = null,
)