package com.caldeirasoft.outcast.data.db.entities

data class DownloadProgress(
  /**
   * Content id
   */
  val feedUrl: String,
  /**
   * Content id
   */
  val guid: String,
  /**
   * Percent downloaded
   */
  val percentDownloaded: Int,
  /**
   * State [DownloadState]
   */
  val state: DownloadState
)