package com.caldeirasoft.outcast.domain.interfaces

import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class StoreFeaturedPage(private val storeData: StoreFeatured)
    : StorePage {
    val label get() = this.storeData.label
    val description get() = this.storeData.description
    val url get() = this.storeData.url
    val artwork get() = this.storeData.artwork
}