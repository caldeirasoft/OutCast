package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import com.caldeirasoft.outcast.data.db.entities.Download
import com.caldeirasoft.outcast.data.db.entities.DownloadState
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.ui.util.DateFormatter.formatRelativeDateTime
import com.caldeirasoft.outcast.ui.util.DurationFormatter.formatDuration
import com.caldeirasoft.outcast.ui.util.applyTextStyleCustom
import com.caldeirasoft.outcast.ui.util.applyTextStyleNullable
import com.google.accompanist.coil.rememberCoilPainter
import java.util.*


@OptIn(ExperimentalStdlibApi::class)
@Composable
fun StoreEpisodeItem(
    modifier: Modifier = Modifier,
    episode: Episode,
    index: Int? = null,
    onEpisodeClick: () -> Unit,
    onThumbnailClick: () -> Unit,
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
        secondaryText = {
            val context = LocalContext.current
            Text(
                text = with(AnnotatedString.Builder()) {
                    append(episode.releaseDateTime.formatRelativeDateTime(context))
                    if (episode.duration != 0) {
                        append(" ◾ ${episode.duration.formatDuration()}")
                    }
                    toAnnotatedString()
                },
            )
        },
        icon = {
            PodcastThumbnail(
                data = episode.artworkUrl,
                modifier = Modifier
                    .size(EpisodeDefaults.LargeThumbnailSize)
                    .clickable(onClick = onThumbnailClick)
            )
        },
    )
}

@Composable
fun QueueEpisodeItem(
    modifier: Modifier = Modifier,
    episode: Episode,
    onEpisodeClick: () -> Unit,
) {
    EpisodeDefaults.EpisodeItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEpisodeClick() },
        icon = {
            PodcastThumbnail(
                data = episode.artworkUrl,
                modifier = Modifier
                    .size(EpisodeDefaults.ThumbnailSize)
                    .clickable(onClick = { })
            )
        },
        text = { Text(text = episode.name, maxLines = 1) },
        releasedTimeText = {
            val context = LocalContext.current
            Text(text = episode.releaseDateTime.formatRelativeDateTime(context))
        },
        actionButtons = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlayButton(episode = episode)
                QueueButton(episode = episode)
            }
        },
    )
}

@Composable
fun EpisodeItem(
    modifier: Modifier = Modifier,
    episode: Episode,
    download: Download? = null,
    onEpisodeClick: (() -> Unit)? = null,
    onPodcastClick: (() -> Unit)? = null,
    onContextMenuClick: (() -> Unit)? = null,
    index: Int? = null,
    showActions: Boolean = true,
) {
    val clickableMod = if (onEpisodeClick != null) {
        Modifier.clickable(onClick = onEpisodeClick)
    } else {
        Modifier
    }
    EpisodeDefaults.EpisodeItem(
        modifier = modifier
            .fillMaxWidth()
            .then(clickableMod),
        icon = {
            PodcastThumbnail(
                data = episode.artworkUrl,
                modifier = Modifier
                    .size(EpisodeDefaults.ThumbnailSize)
                    .clickable(onClick = { onPodcastClick?.invoke() })
            )
        },
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
        releasedTimeText = { },
        actionButtons = {
            if (showActions && onContextMenuClick != null) {
                EpisodeActionButtons(
                    episode = episode,
                    download = download,
                    onContextMenuClick = onContextMenuClick
                )
            }
        },
        overlineText = {
            val context = LocalContext.current
            Text(text = episode
                .releaseDateTime
                .formatRelativeDateTime(context)
                .toUpperCase(Locale.getDefault()))
        },
        descriptionText = {
            episode.description?.let {
                Text(text = HtmlCompat.fromHtml(
                    it,
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                ).toString(), maxLines = 2)
            }
        }
    )
}

@Composable
fun PodcastEpisodeItem(
    modifier: Modifier = Modifier,
    episode: Episode,
    download: Download? = null,
    onEpisodeClick: () -> Unit,
    onContextMenuClick: () -> Unit,
    index: Int? = null,
) {
    EpisodeDefaults.EpisodeItem(
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
        releasedTimeText = { },
        actionButtons = {
            EpisodeActionButtons(
                episode = episode,
                download = download,
                onContextMenuClick = onContextMenuClick
            )
        },
        overlineText = {
            val context = LocalContext.current
            Text(text = episode
                .releaseDateTime
                .formatRelativeDateTime(context)
                .toUpperCase(Locale.getDefault()))
        },
        descriptionText = {
            episode.description?.let {
                Text(text = HtmlCompat.fromHtml(
                    it,
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                ).toString(), maxLines = 2)
            }
        }
    )

    /*
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
    */

}

@Composable
fun EpisodeGridItem(
    modifier: Modifier = Modifier,
    episode: Episode,
) {
    Column(modifier = modifier
        .height(300.dp)) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = ButtonDefaults.outlinedBorder,
            elevation = 0.dp,
            modifier = modifier.fillMaxSize()
        ) {
            Column {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f))
                {
                    Image(
                        painter = rememberCoilPainter(request = episode.artworkUrl),
                        contentDescription = episode.podcastName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth())
                }
                Column(modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 8.dp)
                    .weight(1f)) {
                    Text(
                        text = episode.name,
                        modifier = Modifier.fillMaxWidth(),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 3,
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = with(AnnotatedString.Builder()) {
                            append(episode.podcastName)
                            append(" — ")
                            append(episode.artistName)
                            toAnnotatedString()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.caption
                    )
                }
                Row(modifier = Modifier
                    .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    PlayButton(episode = episode)
                    QueueButton(episode = episode)
                }
            }
        }
    }
}


@Composable
fun EpisodeTrailerItem(
    modifier: Modifier = Modifier,
    episode: Episode,
    onEpisodeClick: () -> Unit,
) {
    EpisodeDefaults.EpisodeItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEpisodeClick() },
        icon = {
            PodcastThumbnail(
                data = episode.artworkUrl,
                modifier = Modifier
                    .size(EpisodeDefaults.ThumbnailSize)
                    .clickable(onClick = { })
            )
        },
        text = { Text(text = episode.name, maxLines = 1) },
        overlineText = {
            val context = LocalContext.current
            Text(text = episode.releaseDateTime.formatRelativeDateTime(context))
        },
        actionButtons = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlayButton(episode = episode)
                QueueButton(episode = episode)
            }
        },
        releasedTimeText = { },
    )
}

@Composable
private fun EpisodeActionButtons(
    modifier: Modifier = Modifier,
    episode: Episode,
    download: Download?,
    onContextMenuClick: () -> Unit,
) {
    //TODO: app bar ?
    val tintColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    Row(modifier = Modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayButton(episode = episode)
        Spacer(modifier = Modifier.weight(1f))
        // queued button
        // favorite button
        if (episode.isSaved) {
            Icon(
                modifier = Modifier.size(16.dp),
                imageVector = Icons.Filled.Bookmark,
                contentDescription = null
            )
        }
        // downloaded button
        EpisodeDownloadButton(
            episode = episode,
            download = download)

        // more button
        IconButton(onClick = onContextMenuClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = tintColor,
            )
        }
    }
}

@Composable
private fun EpisodeDownloadButton(
    modifier: Modifier = Modifier,
    episode: Episode,
    download: Download?
) {
    val tintColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    when (download?.state) {
        DownloadState.CREATED.ordinal ->
            Box(modifier = Modifier.padding(start = 8.dp)) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDownward,
                    contentDescription = null,
                    tint = tintColor,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.Center)
                )
            }
        DownloadState.IN_PROGRESS.ordinal ->
            Box(modifier = Modifier.padding(start = 8.dp)) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    progress = download.progress / 100f
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDownward,
                    contentDescription = null,
                    tint = tintColor,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.Center)
                )
            }
        DownloadState.COMPLETED.ordinal ->
            Box(modifier = Modifier.padding(start = 8.dp)) {
                Icon(
                    imageVector = Icons.Filled.DownloadForOffline,
                    contentDescription = null,
                    tint = tintColor,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.Center)
                )
            }
    }
}

private object EpisodeDefaults {
    // List item related defaults.
    private val MinHeight = 88.dp

    // Default thumbnail size
    val ThumbnailSize = 56.dp

    // Default thumbnail size
    val LargeThumbnailSize = 72.dp

    // Small thumbnail size
    val SmallThumbnailSize = 40.dp

    // Content related defaults.
    private val ContentLeftPadding = 16.dp
    private val ContentRightPadding = 16.dp
    private val ContentInnerPadding = 8.dp
    private val ContentTopPadding = 16.dp
    private val ContentBottomPadding = 8.dp

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
        icon: @Composable (() -> Unit)? = null,
        overlineText: @Composable (() -> Unit)? = null,
        descriptionText: @Composable (() -> Unit)? = null,
        text: @Composable () -> Unit,
        releasedTimeText: @Composable () -> Unit,
        actionButtons: @Composable (() -> Unit),
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
            .fillMaxWidth()
            .padding(
                start = ContentLeftPadding,
                top = ContentTopPadding,
                bottom = ContentInnerPadding
            ))
        {
            icon?.let {
                Box(modifier = Modifier.padding(end = ContentInnerPadding)) {
                    it()
                }
            }

            Column(modifier = Modifier
                .weight(1f))
            {
                Column(modifier = Modifier.padding(end = ContentRightPadding)) {
                    styledOverlineText?.let {
                        it()
                    }
                    styledText()
                    //styledReleasedTimeText()
                    styledDescriptionText?.let {
                        it()
                    }
                }
                actionButtons()
            }

        }
    }
}

