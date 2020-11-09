package com.caldeirasoft.outcast.domain.models

import kotlinx.serialization.Serializable

@Serializable
class StoreIdsPodcastEpisodes(
    var label: String,
    override val itemsIds: List<Long>) : StoreIds, StoreCollection