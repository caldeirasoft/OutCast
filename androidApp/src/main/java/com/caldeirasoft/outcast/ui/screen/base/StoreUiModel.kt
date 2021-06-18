package com.caldeirasoft.outcast.ui.screen.base

import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import kotlinx.datetime.Instant

sealed class StoreUiModel {
    data class StoreUiItem(val item: StoreItem, val index: Int? = null) : StoreUiModel()
    data class TitleItem(val item: StoreItem) : StoreUiModel()
}