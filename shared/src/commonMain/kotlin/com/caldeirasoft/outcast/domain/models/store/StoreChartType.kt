package com.caldeirasoft.outcast.domain.models.store

import kotlinx.serialization.Serializable

@Serializable
enum class StoreChartType {
    PODCAST,
    EPISODE,
}