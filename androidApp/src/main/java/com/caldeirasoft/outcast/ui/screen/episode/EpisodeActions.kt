package com.caldeirasoft.outcast.ui.screen.episode

import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastActions

sealed class EpisodeActions {
    data class SetEpisode(val storeEpisode: StoreEpisode): EpisodeActions()
    object OpenPodcastDetail : EpisodeActions()
    object NavigateUp : EpisodeActions()
}