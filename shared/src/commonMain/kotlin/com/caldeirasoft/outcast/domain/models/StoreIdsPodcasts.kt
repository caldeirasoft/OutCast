package com.caldeirasoft.outcast.domain.models

import kotlinx.serialization.Serializable

@Serializable
class StoreIdsPodcasts(
    var label: String,
    var url: String? = null,
    override val itemsIds: List<Long>) : StoreIds, StoreCollection