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
    @ColumnInfo(name = "sync_on_mobile_data") val syncOnMobileData: Boolean = true,
    @ColumnInfo(name = "episode_limit") val episodeLimit: Int = EpisodeLimitOptions.ONE_MONTH.ordinal,
    @ColumnInfo(name = "allow_notifications") val allowNotifications: Boolean = false,
    @ColumnInfo(name = "notifications_type") val notificationsType: Int = 0,
    @ColumnInfo(name = "notifications_badge") val notificationsBadgeType: Int = 0,
    @ColumnInfo(name = "download_queue_episodes") val downloadQueuedEpisodes: Boolean = true,
    @ColumnInfo(name = "download_queue_length") val downloadQueuedLength: Int = 80,
    @ColumnInfo(name = "delete_played_episodes") val deletePlayedEpisodesDelay: Int = DeleteEpisodesDelayOptions.AFTER_1_DAY.ordinal,
    @ColumnInfo(name = "download_starred_episodes") val downloadStarredEpisodes: Boolean = true,
    @ColumnInfo(name = "download_on_mobile_data") val downloadOnMobileData: Boolean = false,
    @ColumnInfo(name = "skip_back_button") val skipBackButton: Int = SkipOptions.SKIP_15_SECONDS.ordinal,
    @ColumnInfo(name = "skip_forward_button") val skipForwardButton: Int = SkipOptions.SKIP_30_SECONDS.ordinal,
    @ColumnInfo(name = "external_controls") val externalControls: Int = ExternalControlsOptions.SKIP_FORWARD_BACK.ordinal,
    @ColumnInfo(name = "stream_on_mobile_data") val streamOnMobileData: Int = StreamOptions.PLAY.ordinal,
    @ColumnInfo(name = "theme") val theme: Int = Theme.AUTO.ordinal,
    @ColumnInfo(name = "store_country") val storeCountry: String = "",
    @ColumnInfo(name = "last_sync_at") val lastSyncAt: Instant,
    @ColumnInfo(name = "last_refresh_at") val lastRefreshAt: Instant,
) {
    companion object {
        const val TABLE_NAME: String = "settings"

        val Settings.backgroundSyncOption: BackgroundRefreshOptions
            get() = BackgroundRefreshOptions.values()[this.backgroundSync]

        val Settings.episodeLimitOption: EpisodeLimitOptions
            get() = EpisodeLimitOptions.values()[this.episodeLimit]

        val Settings.notificationsTypeOption: Set<NotificationsTypeOptions>
            get() = sequence {
                NotificationsTypeOptions
                    .values()
                    .forEach { option ->
                        this@notificationsTypeOption
                            .notificationsType
                            .and(option.id)
                            .let {
                                if (it > 0)
                                    yield(option)
                            }
                    }
            }.toSet()

        var Settings.notificationsBadgeTypeOption: Set<NotificationsBadgeOptions>
            get() = sequence {
                NotificationsBadgeOptions
                    .values()
                    .forEach { option ->
                        this@notificationsBadgeTypeOption
                            .notificationsBadgeType
                            .and(option.id)
                            .let {
                                if (it > 0)
                                    yield(option)
                            }
                    }
            }.toSet()
            set(value) {
                value
                    .map { it.id }
                    .sum()
            }

        val Settings.deletePlayedEpisodesDelayOption: DeleteEpisodesDelayOptions
            get() = DeleteEpisodesDelayOptions.values()[this.deletePlayedEpisodesDelay]

        val Settings.streamOnMobileDataOption: StreamOptions
            get() = StreamOptions.values()[this.streamOnMobileData]

        val Settings.skipBackButtonOption: SkipOptions
            get() = SkipOptions.values()[this.skipBackButton]

        val Settings.skipForwardButtonOption: SkipOptions
            get() = SkipOptions.values()[this.skipForwardButton]

        val Settings.externalControlsOptions: ExternalControlsOptions
            get() = ExternalControlsOptions.values()[this.externalControls]

        val Settings.themeOptions: Theme
            get() = Theme.values()[this.theme]

        // general
        // notifications
        // downloads
        // delete episode : after listening/1 day after listening/7 days after listening
        // playback
    }
}
