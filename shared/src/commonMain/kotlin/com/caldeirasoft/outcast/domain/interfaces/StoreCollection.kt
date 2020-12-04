package com.caldeirasoft.outcast.domain.interfaces

interface StoreCollection : StoreItem {
    var items: List<StoreItemWithArtwork>
}