@file:UseSerializers(InstantSerializer::class)

package com.caldeirasoft.outcast.ui.screen.store.discover

import android.os.Parcelable
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.UseSerializers


@Parcelize
data class StoreDataArg(
    val id: Long,
    val label: String,
    val url: String = "",
    val genreId: Int? = null,
    val description: String? = null,
    val storeFront: String,
    var storeIds: List<Long> = arrayListOf(),
) : Parcelable {
    fun toStoreData() =
        StoreData(
            id = id,
            label = label,
            url = url,
            genreId = genreId,
            description = description,
            storeFront = storeFront,
            storeIds = storeIds
        )

    companion object {
        fun StoreData.toStoreDataArg() = StoreDataArg(
            id = id,
            label = label,
            url = url,
            genreId = genreId,
            description = description,
            storeFront = storeFront,
            storeIds = storeIds
        )

        fun Genre.toStoreDataArg() = StoreDataArg(
            id = id.toLong(),
            label = name,
            url = url,
            genreId = id,
            storeFront = "",
        )
    }
}