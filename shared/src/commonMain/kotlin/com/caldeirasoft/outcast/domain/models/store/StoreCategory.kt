@file:UseSerializers(com.caldeirasoft.outcast.domain.serializers.InstantSerializer::class)
package com.caldeirasoft.outcast.domain.models.store

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
class StoreCategory(
    val id: Int,
    var name: String,
    val storeFront: String,
    val url: String,
)