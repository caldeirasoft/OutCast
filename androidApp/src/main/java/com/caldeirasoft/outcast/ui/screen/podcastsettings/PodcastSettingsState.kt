package com.caldeirasoft.outcast.ui.screen.podcastsettings

import androidx.datastore.preferences.core.Preferences
import androidx.paging.PagingData
import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.data.common.PodcastPreferenceKeys
import com.caldeirasoft.outcast.data.common.PodcastPreferences
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus

data class PodcastSettingsState(
    val podcast: Podcast,
    val prefs: PodcastPreferences? = null,
) : MavericksState