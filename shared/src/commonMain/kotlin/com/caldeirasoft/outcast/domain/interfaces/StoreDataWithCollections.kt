package com.caldeirasoft.outcast.domain.interfaces

interface StoreDataWithCollections : StoreDataWithLookup {
    val storeList: List<StoreCollection>
}