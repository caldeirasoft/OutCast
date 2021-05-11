package com.caldeirasoft.outcast.ui.screen.library

import androidx.annotation.StringRes
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastActions

enum class LibraryItemType (@StringRes val titleId: Int) {
    LATEST_EPISODES(R.string.library_item_latest),
    SAVED_EPISODES(R.string.library_item_saved),
    SIDELOADS(R.string.library_item_sideloads),
    PODCASTS(R.string.library_item_podcasts)
}