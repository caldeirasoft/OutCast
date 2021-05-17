package com.caldeirasoft.outcast.ui.screen.library

import androidx.annotation.StringRes
import com.caldeirasoft.outcast.R

enum class LibraryItemType (@StringRes val titleId: Int) {
    LATEST_EPISODES(R.string.library_item_latest),
    SAVED_EPISODES(R.string.library_item_saved),
    SIDELOADS(R.string.library_item_sideloads),
    PODCASTS(R.string.library_item_podcasts)
}