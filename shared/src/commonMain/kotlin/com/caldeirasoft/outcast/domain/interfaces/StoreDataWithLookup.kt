package com.caldeirasoft.outcast.domain.interfaces

interface StoreDataWithLookup : StoreData {
    var lookup: Map<Long, StoreItem>
}