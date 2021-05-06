package com.caldeirasoft.outcast.ui.screen.store.topcharts

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus

data class TopChartsState(
    val category: Category? = null,
    val followingStatus: List<Long> = emptyList(),
    val followLoadingStatus: List<Long> = emptyList(),
) : MavericksState


