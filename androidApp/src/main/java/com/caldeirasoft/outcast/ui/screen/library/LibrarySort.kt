package com.caldeirasoft.outcast.ui.screen.library

import androidx.annotation.StringRes
import com.caldeirasoft.outcast.R

enum class LibrarySort (@StringRes val titleId: Int) {
    RECENTLY_FOLLOWED(R.string.library_sort_recently_followed),
    RECENTLY_UPDATED(R.string.library_sort_recently_updated),
    NAME(R.string.library_sort_by_name),
    AUTHOR(R.string.library_sort_by_author)
}