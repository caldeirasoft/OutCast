package com.caldeirasoft.outcast.domain.models.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollectionTopChart
import kotlinx.serialization.Serializable

@Serializable
class StoreCollectionTopEpisodes(
    override val label: String,
    override val genreId: Int,
    override val storeList: List<StoreEpisode>,
    override val storeFront: String,
) : StoreCollectionTopChart<StoreEpisode>