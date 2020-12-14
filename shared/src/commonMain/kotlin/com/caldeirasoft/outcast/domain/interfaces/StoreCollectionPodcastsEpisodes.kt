package com.caldeirasoft.outcast.domain.interfaces

interface StoreCollectionPodcastsEpisodes : StoreCollection {
    var label: String
    var url: String?
    val itemsIds: List<Long>
}