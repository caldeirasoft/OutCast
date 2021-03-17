package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.EpisodeSummary
import com.caldeirasoft.outcast.domain.models.getArtworkUrl
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.DateFormatter.formatRelativeDisplay
import com.caldeirasoft.outcast.ui.util.DurationFormatter.formatDuration
import com.caldeirasoft.outcast.ui.util.applyTextStyleCustom
import com.caldeirasoft.outcast.ui.util.applyTextStyleNullable


@Composable
fun StoreEpisodeItem(
    modifier: Modifier = Modifier,
    episode: Episode,
    index: Int? = null,
    onEpisodeClick: () -> Unit,
    onPodcastClick: () -> Unit,
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEpisodeClick() },
        text = {
            Text(
                text = with(AnnotatedString.Builder()) {
                    if (index != null) {
                        pushStyle(SpanStyle(color = Color.Red))
                        append("${index}. ")
                        pop()
                    }
                    append(episode.name)
                    toAnnotatedString()
                },
                maxLines = 2,
            )
        },
        overlineText = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                ProvideTextStyle(typography.caption) {
                    Text(text = episode.podcastName, maxLines = 1)
                }
            }
        },
        secondaryText = {
            val context = LocalContext.current
            Text(
                text = with(AnnotatedString.Builder()) {
                    append(episode.duration.formatDuration())
                    append(" ● ")
                    append(episode.releaseDateTime.formatRelativeDisplay(context))
                    toAnnotatedString()
                },
                maxLines = 3,
            )
        },
        icon = {
            PodcastThumbnail(
                imageModel = episode.getArtworkUrl(),
                modifier = Modifier
                    .size(EpisodeDefaults.ThumbnailSize)
                    .clickable(onClick = { })
            )
        }
    )
}

@Composable
fun QueueEpisodeItem(
    modifier: Modifier = Modifier,
    episode: Episode,
    onEpisodeClick: () -> Unit,
) {
    EpisodeDefaults.EpisodeItem(
        modifier = modifier,
        icon = {
            PodcastThumbnail(
                imageModel = episode.getArtworkUrl(),
                modifier = Modifier
                    .size(EpisodeDefaults.ThumbnailSize)
                    .clickable(onClick = { })
            )
        },
        text = { Text(text = episode.name, maxLines = 1) },
        releasedTimeText = {
            val context = LocalContext.current
            Text(text = episode.releaseDateTime.formatRelativeDisplay(context))
        },
        actionButtons = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlayButton(episode = episode)
                QueueButton(episode = episode)
            }
        },
        onEpisodeClick = onEpisodeClick
    )
}

@Composable
fun EpisodeItem(
    modifier: Modifier = Modifier,
    episode: Episode,
    onEpisodeClick: () -> Unit,
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEpisodeClick() },
        text = {
            Text(text = episode.name, maxLines = 2)
        },
        secondaryText = {
            val context = LocalContext.current
            Text(
                text = with(AnnotatedString.Builder()) {
                    append(episode.duration.formatDuration())
                    append(" ● ")
                    append(episode.releaseDateTime.formatRelativeDisplay(context))
                    append("\n")
                    episode.description?.let {
                        append(it)
                    }
                    toAnnotatedString()
                },
                maxLines = 3,
            )
        },
        icon = {
            PodcastThumbnail(
                imageModel = episode.getArtworkUrl(),
                modifier = Modifier
                    .size(EpisodeDefaults.SmallThumbnailSize)
                    .clickable(onClick = { })
            )
        }
    )
}

@Composable
fun EpisodeTrailerItem(
    modifier: Modifier = Modifier,
    episode: Episode,
    onEpisodeClick: () -> Unit,
) {
    EpisodeDefaults.EpisodeItem(
        modifier = modifier,
        icon = {
            PodcastThumbnail(
                imageModel = episode.getArtworkUrl(),
                modifier = Modifier
                    .size(EpisodeDefaults.ThumbnailSize)
                    .clickable(onClick = { })
            )
        },
        text = { Text(text = episode.name, maxLines = 1) },
        overlineText = {
            val context = LocalContext.current
            Text(text = episode.releaseDateTime.formatRelativeDisplay(context))
        },
        actionButtons = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlayButton(episode = episode)
                QueueButton(episode = episode)
            }
        },
        releasedTimeText = { },
        onEpisodeClick = onEpisodeClick
    )
}

@Composable
fun EpisodeTrailerItem(
    modifier: Modifier = Modifier,
    episode: EpisodeSummary,
    onEpisodeClick: () -> Unit,
) {
    EpisodeDefaults.EpisodeItem(
        modifier = modifier,
        icon = {
            PodcastThumbnail(
                imageModel = episode.getArtworkUrl(),
                modifier = Modifier
                    .size(EpisodeDefaults.ThumbnailSize)
                    .clickable(onClick = { })
            )
        },
        text = { Text(text = episode.name, maxLines = 1) },
        overlineText = {
            val context = LocalContext.current
            Text(text = episode.releaseDateTime.formatRelativeDisplay(context))
        },
        actionButtons = { },
        releasedTimeText = { },
        onEpisodeClick = onEpisodeClick
    )
}


private object EpisodeDefaults {
    // List item related defaults.
    private val MinHeight = 88.dp

    // Default thumbnail size
    val ThumbnailSize = 56.dp

    // Small thumbnail size
    val SmallThumbnailSize = 40.dp

    // Content related defaults.
    private val ContentLeftPadding = 16.dp
    private val ContentRightPadding = 16.dp
    private val ContentInnerPadding = 8.dp
    private val ContentTopPadding = 16.dp

    @Composable
    fun CardItem(
        modifier: Modifier = Modifier,
        itemContent: @Composable () -> Unit,
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            border = ButtonDefaults.outlinedBorder,
            elevation = 0.dp,
            modifier = modifier.fillMaxWidth()
        ) {
            itemContent()
        }
    }

    @Composable
    fun EpisodeItem(
        modifier: Modifier = Modifier,
        icon: @Composable () -> Unit,
        overlineText: @Composable (() -> Unit)? = null,
        descriptionText: @Composable (() -> Unit)? = null,
        text: @Composable () -> Unit,
        releasedTimeText: @Composable () -> Unit,
        actionButtons: @Composable (() -> Unit),
        showActionButtonsOnTap: Boolean = true,
        showActionButtons: Boolean = false,
        onEpisodeClick: () -> Unit,
    ) {
        val typography = MaterialTheme.typography

        val styledText =
            applyTextStyleCustom(typography.subtitle1.copy(fontWeight = FontWeight.Medium),
                ContentAlpha.high,
                text)
        val styledOverlineText =
            applyTextStyleNullable(typography.caption, ContentAlpha.medium, overlineText)
        val styledReleasedTimeText =
            applyTextStyleCustom(typography.body2, ContentAlpha.high, releasedTimeText)
        val styledDescriptionText =
            applyTextStyleNullable(typography.body2, ContentAlpha.high, descriptionText)

        Row(modifier = modifier
            .clickable { onEpisodeClick() }
            .fillMaxWidth()
            .padding(bottom = ContentInnerPadding))
        {
            icon()

            Column(modifier = Modifier
                .weight(1f)
                .padding(start = ContentInnerPadding))
            {
                styledOverlineText?.let {
                    it()
                }
                styledText()
                styledReleasedTimeText()
                styledDescriptionText?.let {
                    it()
                }
            }

            //actionButtons()
        }
    }

    enum class EpisodeSpot {
        QUEUE,
        INBOX,
        LIBRARY
    }
}

