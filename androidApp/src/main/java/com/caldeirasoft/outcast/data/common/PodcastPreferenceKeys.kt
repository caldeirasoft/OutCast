package com.caldeirasoft.outcast.data.common

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_CUSTOM_PLAYBACK_EFFECTS
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_CUSTOM_PLAYBACK_SPEED
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_EPISODE_LIMIT
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_NEW_EPISODES
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_NOTIFICATIONS
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_SKIP_ENDING
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_SKIP_INTROS
import com.caldeirasoft.outcast.data.common.Constants.Companion.PREFERENCES_TRIM_SILENCE

class PodcastPreferenceKeys(val podcastId: Long) {
    val newEpisodes = stringPreferencesKey("$podcastId:$PREFERENCES_NEW_EPISODES")
    val notifications = booleanPreferencesKey("$podcastId:$PREFERENCES_NOTIFICATIONS")
    val episodeLimit = stringPreferencesKey("$podcastId:$PREFERENCES_EPISODE_LIMIT")
    val customPlaybackEffects =
        booleanPreferencesKey("$podcastId:$PREFERENCES_CUSTOM_PLAYBACK_EFFECTS")
    val customPlaybackSpeed =
        floatPreferencesKey("$podcastId:$PREFERENCES_CUSTOM_PLAYBACK_SPEED")
    val trimSilence = booleanPreferencesKey("$podcastId:$PREFERENCES_TRIM_SILENCE")
    val skipIntro = intPreferencesKey("$podcastId:$PREFERENCES_SKIP_INTROS")
    val skipEnding = intPreferencesKey("$podcastId:$PREFERENCES_SKIP_ENDING")
}