package com.caldeirasoft.outcast.domain.interfaces

import kotlinx.datetime.Instant

interface StorePage : StoreData {
    var lookup: Map<Long, StoreItemWithArtwork>
    val storeFront: String
    val timestamp: Instant
}