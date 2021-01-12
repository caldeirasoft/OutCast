package com.caldeirasoft.outcast.domain.interfaces

interface StoreCollectionTopChart <T : StoreItem> : StoreCollection {
    val label: String
    val genreId: Int
    val storeList: List<T>
    override val storeFront: String
}