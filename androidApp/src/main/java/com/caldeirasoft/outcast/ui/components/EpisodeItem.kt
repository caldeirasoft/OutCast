package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.ui.util.DateFormatter.formatRelativeDisplay
import com.caldeirasoft.outcast.ui.util.applyTextStyleCustom
import com.caldeirasoft.outcast.ui.util.applyTextStyleNullable


@Composable
fun EpisodeCardItemWithArtwork(
    modifier: Modifier = Modifier,
    storeEpisode: StoreEpisode,
    index: Int? = null,
    onEpisodeClick: () -> Unit,
    onPodcastClick: () -> Unit,
) {
    EpisodeDefaults.CardItem(
        modifier = modifier
            .clickable(onClick = onEpisodeClick),
    ) {
        EpisodeDefaults.EpisodeItem(
            overlineText = {
                EpisodeDefaults.EpisodeItemArtworkHeader(
                    icon = {
                        PodcastThumbnail(
                            imageModel = storeEpisode.getArtworkUrl(),
                            modifier = Modifier
                                .preferredSize(EpisodeDefaults.ThumbnailSize)
                                .clickable(onClick = onPodcastClick)
                        )
                    },
                    podcastText = {
                        Text(text = storeEpisode.podcastName, maxLines = 1)
                    },
                    text = {
                        Text(text = AnnotatedString.Builder()
                            .apply {
                                if (index != null) {
                                    withStyle(SpanStyle(color = Color.Red)) {
                                        append("${index}. ")
                                    }
                                }
                                append(storeEpisode.name)
                            }.toAnnotatedString(),
                            maxLines = 2,
                            modifier = Modifier.height(45.dp)
                        )
                    }
                )
            },
            actionButtons = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlayButton(storeEpisode = storeEpisode)
                    QueueButton(storeEpisode = storeEpisode)
                }
            }
        )
    }
}

@Composable
fun EpisodeItem(
    modifier: Modifier = Modifier,
    storeEpisode: StoreEpisode,
    onEpisodeClick: () -> Unit,
) {
    EpisodeDefaults.EpisodeItem(
        modifier = modifier
            .clickable(onClick = onEpisodeClick),
        text = { Text(text = storeEpisode.name, maxLines = 2, fontSize = 14.sp) },
        overlineText = {
            val context = LocalContext.current
            Text(text = storeEpisode.releaseDateTime.formatRelativeDisplay(context))
        },
        actionButtons = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlayButton(storeEpisode = storeEpisode)
                QueueButton(storeEpisode = storeEpisode)
            }
        }
    )
}

@Composable
fun EpisodeItemWithDesc(
    modifier: Modifier = Modifier,
    storeEpisode: StoreEpisode,
    onEpisodeClick: () -> Unit,
) {
    EpisodeDefaults.EpisodeItem(
        modifier = modifier
            .clickable(onClick = onEpisodeClick),
        text = { Text(text = storeEpisode.name, maxLines = 1) },
        overlineText = {
            val context = LocalContext.current
            Text(text = storeEpisode.releaseDateTime.formatRelativeDisplay(context))
        },
        descriptionText = { Text(text = storeEpisode.description.orEmpty(), maxLines = 2) },
        actionButtons = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlayButton(storeEpisode = storeEpisode)
                QueueButton(storeEpisode = storeEpisode)
            }
        }
    )
}

@Composable
fun EpisodeTrailerItem(
    modifier: Modifier = Modifier,
    storeEpisode: StoreEpisode,
    onEpisodeClick: () -> Unit,
) {
    EpisodeDefaults.EpisodeItem(
        modifier = modifier
            .clickable(onClick = onEpisodeClick),
        text = { Text(text = storeEpisode.name, maxLines = 1) },
        overlineText = {
            val context = LocalContext.current
            Text(text = storeEpisode.releaseDateTime.formatRelativeDisplay(context))
        },
        actionButtons = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlayButton(storeEpisode = storeEpisode)
                QueueButton(storeEpisode = storeEpisode)
            }
        }
    )
}

@Composable
fun EpisodeItemWithDescAndArtwork(
    modifier: Modifier = Modifier,
    storeEpisode: StoreEpisode,
    onEpisodeClick: () -> Unit,
    onPodcastClick: () -> Unit,
) {
    EpisodeDefaults.EpisodeItem(
        modifier = modifier
            .clickable(onClick = onEpisodeClick),
        overlineText = {
            EpisodeDefaults.EpisodeItemArtworkHeader(
                icon = {
                    PodcastThumbnail(
                        imageModel = storeEpisode.getArtworkUrl(),
                        modifier = Modifier
                            .preferredSize(EpisodeDefaults.ThumbnailSize)
                            .clickable(onClick = onPodcastClick)
                    )
                },
                podcastText = {
                    Text(text = storeEpisode.podcastName, maxLines = 1)
                },
                text = {
                    Text(text = storeEpisode.name, maxLines = 2)
                }
            )
        },
        descriptionText = { Text(text = storeEpisode.description.orEmpty(), maxLines = 2) },
        actionButtons = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlayButton(storeEpisode = storeEpisode)
                QueueButton(storeEpisode = storeEpisode)
            }
        }
    )
}

@Composable
fun EpisodeItemWithArtwork(
    modifier: Modifier = Modifier,
    storeEpisode: StoreEpisode,
    index: Int? = null,
    onEpisodeClick: () -> Unit,
    onPodcastClick: () -> Unit,
) {
    EpisodeDefaults.EpisodeItem(
        modifier = modifier
            .clickable(onClick = onEpisodeClick),
        overlineText = {
            EpisodeDefaults.EpisodeItemArtworkHeader(
                icon = {
                    PodcastThumbnail(
                        imageModel = storeEpisode.getArtworkUrl(),
                        modifier = Modifier
                            .preferredSize(EpisodeDefaults.ThumbnailSize)
                            .clickable(onClick = onPodcastClick)
                    )
                },
                podcastText = {
                    Text(text = storeEpisode.podcastName, maxLines = 1)
                },
                text = {
                    Text(text = AnnotatedString.Builder()
                        .apply {
                            if (index != null) {
                                withStyle(SpanStyle(color = Color.Red)) {
                                    append("${index}. ")
                                }
                            }
                            append(storeEpisode.name)
                        }.toAnnotatedString(),
                        maxLines = 2)
                }
            )
        },
        actionButtons = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlayButton(storeEpisode = storeEpisode)
                QueueButton(storeEpisode = storeEpisode)
            }
        }
    )
}

@Composable
private fun EpisodeItemArtworkHeader(
    modifier: Modifier = Modifier,
    storeEpisode: StoreEpisode)
{
    EpisodeDefaults.EpisodeItemArtworkHeader(
        modifier = modifier,
        icon = {
            PodcastThumbnail(
                imageModel = storeEpisode.getArtworkUrl(),
                modifier = Modifier
                    .preferredSize(EpisodeDefaults.ThumbnailSize)
            )
        },
        podcastText = {
            Text(text = storeEpisode.podcastName, maxLines = 1)
        },
        releasedTimeText = {
            val context = LocalContext.current
            Text(text = storeEpisode.releaseDateTime.formatRelativeDisplay(context), maxLines = 1)
        },
        text = {
            Text(text = storeEpisode.name)
        }
    )
}

private object EpisodeDefaults {
    // List item related defaults.
    private val MinHeight = 88.dp

    // Default thumbnail size
    val ThumbnailSize = 56.dp

    // Content related defaults.
    private val ContentLeftPadding = 16.dp
    private val ContentRightPadding = 16.dp
    private val ContentInnerPadding = 8.dp
    private val ContentTopPadding = 16.dp

    @Composable
    fun CardItem(
        modifier: Modifier = Modifier,
        itemContent: @Composable () -> Unit
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
        overlineText: @Composable () -> Unit,
        descriptionText: @Composable (() -> Unit)? = null,
        text: (@Composable () -> Unit)? = null,
        actionButtons: @Composable (() -> Unit),
    )
    {
        val typography = MaterialTheme.typography

        val styledText = applyTextStyleNullable(typography.subtitle1.copy(fontWeight = FontWeight.Medium), ContentAlpha.high, text)
        val styledOverlineText = applyTextStyleCustom(typography.caption, ContentAlpha.medium, overlineText)
        val styledDescriptionText = applyTextStyleNullable(typography.body2, ContentAlpha.high, descriptionText)

        Column(modifier
            .fillMaxWidth()
            .preferredHeightIn(min = MinHeight)
            .padding(start = ContentLeftPadding, end = ContentRightPadding, top = ContentTopPadding))
        {
            styledOverlineText()
            styledText?.let {
                it()
            }
            styledDescriptionText?.let {
                it()
            }

            actionButtons()
        }
    }

    @Composable
    fun EpisodeItemArtworkHeader(
        modifier: Modifier = Modifier,
        icon: @Composable () -> Unit,
        podcastText: (@Composable () -> Unit)? = null,
        releasedTimeText: @Composable (() -> Unit)? = null,
        text: @Composable () -> Unit,
    )
    {
        val typography = MaterialTheme.typography

        val styledPodcastText = applyTextStyleNullable(typography.caption, ContentAlpha.medium, podcastText)
        val styledReleasedTimeText = applyTextStyleNullable(typography.caption, ContentAlpha.medium, releasedTimeText)
        val styledText = applyTextStyleCustom(typography.subtitle1.copy(fontWeight = FontWeight.Medium), ContentAlpha.high, text)

        Row(modifier = modifier
            .fillMaxWidth()
            .padding(bottom = ContentInnerPadding))
        {
            Box(modifier = Modifier.preferredSize(64.dp)) {
                icon()
            }

            Column(modifier = Modifier
                .weight(1f)
                .padding(start = ContentInnerPadding))
            {
                styledReleasedTimeText?.let {
                    it()
                }
                styledText()
                styledPodcastText?.let {
                    it()
                }
            }
        }
    }


    @Composable
    fun LibraryEpisodeItem(
        modifier: Modifier = Modifier,
        icon: @Composable () -> Unit,
        overlineText: @Composable () -> Unit,
        actionButtons: @Composable (() -> Unit),
        text: @Composable () -> Unit) {

        val typography = MaterialTheme.typography

        val styledText = applyTextStyleCustom(typography.subtitle1, ContentAlpha.high, text)
        val styledOverlineText = applyTextStyleCustom(typography.caption, ContentAlpha.high, overlineText)

        Column(modifier
            .preferredHeightIn(min = MinHeight)
            .padding(start = ContentLeftPadding, end = ContentRightPadding, top = ContentTopPadding))
        {
            Row(modifier = Modifier
                .fillMaxWidth())
            {
                icon()

                Column(modifier = Modifier
                    .weight(1f)
                    .padding(start = ContentInnerPadding))
                {
                    styledOverlineText()
                    styledText()
                }
            }

            actionButtons()
        }
    }

}

