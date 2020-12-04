package com.caldeirasoft.outcast.domain.interfaces

interface StorePage : StoreData {
    var lookup: Map<Long, StoreItemWithArtwork>
    val storeFront: String
}