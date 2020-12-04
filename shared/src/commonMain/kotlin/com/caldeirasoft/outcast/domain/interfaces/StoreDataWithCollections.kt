package com.caldeirasoft.outcast.domain.interfaces

interface StoreDataWithCollections : StorePage {
    val storeList: List<StoreCollection>
}