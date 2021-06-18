package com.caldeirasoft.outcast.ui.screen.search_results

import com.caldeirasoft.outcast.data.common.Constants.Companion.DEFAULT_GENRE
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.store.StoreCategory
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.ui.screen.base.SearchUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
data class SearchResultsState(
    val queryHint: String = "",
    val query: String = "",
    val followingStatus: List<Long> = emptyList(),
    val followLoadingStatus: List<Long> = emptyList(),
    val podcasts: List<Podcast> = emptyList(),
    val episodes: List<Episode> = emptyList(),
    val hints: List<SearchUiModel> = emptyList()
)