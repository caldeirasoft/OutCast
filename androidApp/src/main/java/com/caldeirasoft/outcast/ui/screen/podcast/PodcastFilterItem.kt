package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.annotation.StringRes
import com.caldeirasoft.outcast.R

enum class PodcastFilterItem(
    @StringRes val titleId: Int,
) {
    All(R.string.filter_all),
    Inbox(R.string.filter_inbox),
    Favorites(R.string.filter_favorites),
    History(R.string.filter_history),
}
