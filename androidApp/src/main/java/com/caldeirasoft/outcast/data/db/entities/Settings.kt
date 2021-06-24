@file:UseSerializers(InstantSerializer::class)
package com.caldeirasoft.outcast.data.db.entities

import androidx.room.*
import com.caldeirasoft.outcast.domain.enums.*
import com.caldeirasoft.outcast.domain.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
@Entity(
  tableName = Settings.TABLE_NAME,
)
data class Settings(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "id") val id: Int = 1,
    @ColumnInfo(name = "sync_podcasts") val syncPodcasts: Boolean = true,
    @ColumnInfo(name = "background_sync") val backgroundSync: Int = BackgroundRefreshOptions.EVERY_1_HOUR.ordinal,
    @ColumnInfo(name = "sync_with_cloud") val syncWithCloud: Boolean = true,
    @ColumnInfo(name = "episode_limit") val episodeLimit: Int = EpisodeLimitOptions.ONE_MONTH.ordinal,
    @ColumnInfo(name = "download_queue_episodes") val downloadQueuedEpisodes: Boolean = true,
    @ColumnInfo(name = "download_saved_episodes") val downloadSavedEpisodes: Boolean = true,
    @ColumnInfo(name = "delete_played_episodes") val deletePlayedEpisodesDelay: Int = DeleteEpisodesDelay.AFTER_1_DAY.ordinal,
    @ColumnInfo(name = "stream_on_mobile_data") val streamOnMobileData: Int = StreamOptions.PLAY.ordinal,
    @ColumnInfo(name = "sync_on_mobile_data") val syncOnMobileData: Boolean = true,
    @ColumnInfo(name = "download_on_mobile_data") val downloadOnMobileData: Boolean = false,
    @ColumnInfo(name = "skip_back_button") val skipBackButton: Int = SkipOptions.SKIP_15_SECONDS.ordinal,
    @ColumnInfo(name = "skip_forward_button") val skipForwardButton: Int = SkipOptions.SKIP_30_SECONDS.ordinal,
    @ColumnInfo(name = "external_controls") val externalControlsOptions: Int = ExternalControlsOptions.SKIP_FORWARD_BACK.ordinal,
    @ColumnInfo(name = "store_country") val storeCountry: String = "",
    @ColumnInfo(name = "last_sync_at") val lastSyncAt: Instant,
    @ColumnInfo(name = "last_refresh_at") val lastRefreshAt: Instant,
    @ColumnInfo(name = "theme") val theme: Int = Theme.AUTO.ordinal,
) {
  companion object {
    const val TABLE_NAME: String = "settings"

    val Settings.backgroundSyncOption: BackgroundRefreshOptions
      get() = BackgroundRefreshOptions.values()[this.backgroundSync]

    val Settings.episodeLimitOption: EpisodeLimitOptions
      get() = EpisodeLimitOptions.values()[this.episodeLimit]

    val Settings.deletePlayedEpisodesDelayOption: DeleteEpisodesDelay
      get() = DeleteEpisodesDelay.values()[this.deletePlayedEpisodesDelay]

    val Settings.streamOnMobileDataOption: StreamOptions
      get() = StreamOptions.values()[this.streamOnMobileData]

    val Settings.skipBackButtonOption: SkipOptions
      get() = SkipOptions.values()[this.skipBackButton]

    val Settings.skipForwardButtonOption: SkipOptions
      get() = SkipOptions.values()[this.skipForwardButton]

    val Settings.externalControlsOptionsOption: ExternalControlsOptions
      get() = ExternalControlsOptions.values()[this.externalControlsOptions]

    // general
    // notifications
    // downloads
      // delete episode : after listening/1 day after listening/7 days after listening
    // playback
  }
}
