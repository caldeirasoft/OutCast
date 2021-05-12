package com.caldeirasoft.outcast.ui.screen.library

import com.caldeirasoft.outcast.data.db.entities.Podcast
import kotlinx.datetime.Instant

data class LibraryState(
    val podcasts: List<Podcast> = emptyList(),
    val sortBy: LibrarySort = LibrarySort.RECENTLY_UPDATED,
    val sortByDesc: Boolean = false,
    val displayAsGrid: Boolean = false,
    val savedEpisodesCount: Int = 0,
) {
    val sortedPodcasts: List<Podcast>
    get() = when(sortBy) {
        LibrarySort.NAME -> podcasts.sortedBy { it.name }
        LibrarySort.AUTHOR -> podcasts.sortedBy { it.artistName }
        LibrarySort.RECENTLY_FOLLOWED -> podcasts.sortedBy { it.updatedAt.epochSeconds }
        LibrarySort.RECENTLY_UPDATED -> podcasts.sortedBy { it.updatedAt.epochSeconds }
    }.let {
        if (sortByDesc)
            it.reversed()
        else it
    }

    val newEpisodesUpdatedAt: Instant?
        get() = podcasts.maxOfOrNull { it.releaseDateTime }
}