package com.caldeirasoft.outcast.domain.models

import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork

fun Episode.getArtworkUrl(): String =
    StoreItemArtwork.artworkUrl(artwork, 200, 200)