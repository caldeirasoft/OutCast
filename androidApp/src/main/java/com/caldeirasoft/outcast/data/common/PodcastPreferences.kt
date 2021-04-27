package com.caldeirasoft.outcast.data.common

data class PodcastPreferences (
    val newEpisodes: String,
    val notifications: Boolean,
    val episodeLimit: String,
    val customPlaybackEffects: Boolean,
    val customPlaybackSpeed: Float,
    val trimSilence: Boolean,
    val skipIntro: Int,
    val skipEnding: Int,
)