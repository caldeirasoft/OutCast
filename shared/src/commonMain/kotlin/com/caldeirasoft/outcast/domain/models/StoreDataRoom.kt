package com.caldeirasoft.outcast.domain.models

import kotlinx.serialization.Serializable

@Serializable
class StoreDataRoom(
    var id: String,
    var label: String,
    var description: String? = null,
    var artwork: Artwork? = null,
    var storeIds: List<Long> = arrayListOf(),
    override var lookup: Map<Long, StoreItem> = mutableMapOf()
) : StoreDataWithLookup