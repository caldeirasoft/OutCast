package com.caldeirasoft.outcast.domain.models

import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionRooms(
    var label: String,
    override var items: List<StoreItem>) : StoreCollection {
}