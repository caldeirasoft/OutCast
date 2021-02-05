package com.caldeirasoft.outcast.domain.interfaces

import kotlinx.datetime.Instant

interface StorePage {
    val storeFront: String
    val lookup: Map<Long, StoreItemWithArtwork>
    val timestamp: Instant
}