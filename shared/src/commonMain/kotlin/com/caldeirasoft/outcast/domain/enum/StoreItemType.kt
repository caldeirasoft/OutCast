package com.caldeirasoft.outcast.domain.enum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class StoreItemType {
    PODCAST,
    EPISODE,
}