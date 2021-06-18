package com.caldeirasoft.outcast.ui.screen.base

import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import kotlinx.datetime.Instant

sealed class SearchUiModel {
    data class HistoryItem(val item: String) : SearchUiModel()
    data class HintItem(val item: String) : SearchUiModel()
}