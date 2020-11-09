package com.caldeirasoft.outcast.domain.models

interface StoreDataWithLookup  : StoreData {
    var lookup: Map<Long, StoreItem>
}