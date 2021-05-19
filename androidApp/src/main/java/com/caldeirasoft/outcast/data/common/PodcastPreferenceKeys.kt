package com.caldeirasoft.outcast.data.common

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_CUSTOM_PLAYBACK_EFFECTS
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_CUSTOM_PLAYBACK_SPEED
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_EPISODE_LIMIT
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_FILTER
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_NOTIFICATIONS
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_SKIP_ENDING
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_SKIP_INTROS
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_SORT_ORDER
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_TRIM_SILENCE

class PodcastPreferenceKeys(val feedUrl: String) {
    val sortOrder = stringPreferencesKey("$feedUrl:$PREFERENCES_SORT_ORDER")
    val filter = stringPreferencesKey("$feedUrl:$PREFERENCES_FILTER")
    val notifications = booleanPreferencesKey("$feedUrl:$PREFERENCES_NOTIFICATIONS")
    val episodeLimit = stringPreferencesKey("$feedUrl:$PREFERENCES_EPISODE_LIMIT")
    val customPlaybackEffects =
        booleanPreferencesKey("$feedUrl:$PREFERENCES_CUSTOM_PLAYBACK_EFFECTS")
    val customPlaybackSpeed =
        floatPreferencesKey("$feedUrl:$PREFERENCES_CUSTOM_PLAYBACK_SPEED")
    val trimSilence = booleanPreferencesKey("$feedUrl:$PREFERENCES_TRIM_SILENCE")
    val skipIntro = intPreferencesKey("$feedUrl:$PREFERENCES_SKIP_INTROS")
    val skipEnding = intPreferencesKey("$feedUrl:$PREFERENCES_SKIP_ENDING")
}