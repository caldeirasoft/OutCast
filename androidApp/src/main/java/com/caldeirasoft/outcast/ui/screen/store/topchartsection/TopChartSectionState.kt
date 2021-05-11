package com.caldeirasoft.outcast.ui.screen.store.topchartsection

import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.models.Category

data class TopChartSectionState(
    val storeItemType: StoreItemType,
    val category: Category? = null,
    val followingStatus: List<Long> = emptyList(),
    val followLoadingStatus: List<Long> = emptyList(),
) {
    constructor(itemType: StoreItemType) : this(storeItemType = itemType)
}


