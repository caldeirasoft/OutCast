package com.caldeirasoft.outcast.data.db.entities

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class PodcastWithCount(
  val feedUrl: String = "",
  val name: String = "",
  val artworkUrl: String = "",
  val count: Int = 0,
) : Parcelable