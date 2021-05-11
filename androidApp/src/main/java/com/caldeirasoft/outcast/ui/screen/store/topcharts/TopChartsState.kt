package com.caldeirasoft.outcast.ui.screen.store.topcharts

import com.caldeirasoft.outcast.domain.models.Category

data class TopChartsState(
    val category: Category? = null,
    val followingStatus: List<Long> = emptyList(),
    val followLoadingStatus: List<Long> = emptyList(),
)


