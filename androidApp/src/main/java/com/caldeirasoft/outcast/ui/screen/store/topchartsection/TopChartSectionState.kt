package com.caldeirasoft.outcast.ui.screen.store.topchartsection

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus

data class TopChartSectionState(
    val storeItemType: StoreItemType,
    val category: Category? = null,
    val followingStatus: List<Long> = emptyList(),
    val followLoadingStatus: List<Long> = emptyList(),
) : MavericksState {
    constructor(itemType: StoreItemType) : this(storeItemType = itemType)
}


