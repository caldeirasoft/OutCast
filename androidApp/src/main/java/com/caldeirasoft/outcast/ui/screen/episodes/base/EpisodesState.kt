package com.caldeirasoft.outcast.ui.screen.episodes.base

import androidx.datastore.preferences.core.Preferences
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus

data class EpisodesState(
    val error: Throwable? = null,
    val episodes: List<Episode> = emptyList(),
    val category: Category? = null,
    val categories: List<Category> = emptyList()
)