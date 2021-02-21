package com.caldeirasoft.outcast.domain.interfaces

interface StorePageWithCollection  : StorePage {
    val storeList: MutableList<StoreCollection>
}