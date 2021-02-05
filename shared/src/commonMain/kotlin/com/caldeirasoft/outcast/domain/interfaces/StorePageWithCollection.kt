package com.caldeirasoft.outcast.domain.interfaces

import kotlinx.datetime.Instant

interface StorePageWithCollection  : StorePage {
    val storeList: MutableList<StoreCollection>
}