package com.caldeirasoft.outcast.domain.models

import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionRooms(
    var label: String,
    val items: List<StoreItem>) : StoreCollection {
}