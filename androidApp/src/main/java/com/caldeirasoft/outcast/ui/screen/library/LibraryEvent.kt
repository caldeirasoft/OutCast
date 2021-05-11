package com.caldeirasoft.outcast.ui.screen.library

import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.store.StoreData

sealed class LibraryEvent {
    data class OpenPodcastContextMenu(val podcast: Podcast) : LibraryEvent()
    data class OpenSortByBottomSheet(val sort: LibrarySort, val sortByDesc: Boolean) : LibraryEvent()
}