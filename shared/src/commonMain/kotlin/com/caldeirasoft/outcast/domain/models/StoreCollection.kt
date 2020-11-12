package com.caldeirasoft.outcast.domain.models

import kotlinx.serialization.Serializable

interface StoreCollection {
    var items: List<StoreItem>
}