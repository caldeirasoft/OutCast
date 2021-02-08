package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Ambient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.semantics.semantics
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
fun StoreEpisodeItem(
    storeEpisode: StoreEpisode,
    index: Int? = null,
    onEpisodeClick: () -> Unit,
    onPodcastClick: () -> Unit
) {
    EpisodeDefaults.EpisodeItem(
        modifier = Modifier
            .clickable(onClick = onEpisodeClick),
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
        },
        overlineText = {
            PodcastEpisodeItemHeader(
                modifier = Modifier.clickable(onClick = onPodcastClick),
                storeEpisode = storeEpisode
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
fun StoreEpisodeCardItem(
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
                    modifier = Modifier.height(40.dp)
                )
            },
            overlineText = {
                PodcastEpisodeItemHeader(
                    modifier = Modifier.clickable(onClick = onPodcastClick),
                    storeEpisode = storeEpisode
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
            val context = AmbientContext.current
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
fun EpisodeDetailItem(
    modifier: Modifier = Modifier,
    storeEpisode: StoreEpisode,
    onEpisodeClick: () -> Unit,
) {
    EpisodeDefaults.EpisodeItem(
        modifier = modifier
            .clickable(onClick = onEpisodeClick),
        text = { Text(text = storeEpisode.name, maxLines = 1) },
        overlineText = {
            val context = AmbientContext.current
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
fun TrailerItem(
    modifier: Modifier = Modifier,
    storeEpisode: StoreEpisode,
    onEpisodeClick: () -> Unit,
) {
    EpisodeDefaults.EpisodeItem(
        modifier = modifier
            .clickable(onClick = onEpisodeClick),
        text = { Text(text = storeEpisode.name, maxLines = 1) },
        overlineText = {
            val context = AmbientContext.current
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
fun PodcastEpisodeCardItem(
    modifier: Modifier = Modifier,
    storeEpisode: StoreEpisode,
    onEpisodeClick: () -> Unit,
    onPodcastClick: () -> Unit,
) {
    EpisodeDefaults.CardItem(
        modifier = modifier
            .clickable(onClick = onEpisodeClick),
    ) {
        EpisodeDefaults.EpisodeItem(
            text = { Text(text = storeEpisode.name, maxLines = 1) },
            overlineText = {
                PodcastEpisodeItemHeader(
                    modifier = Modifier.clickable(onClick = onPodcastClick),
                    storeEpisode = storeEpisode
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
}

@Composable
fun LibraryEpisodeItem(
    storeEpisode: StoreEpisode,
    onEpisodeClick: () -> Unit,
    onPodcastClick: () -> Unit,
) {
    EpisodeDefaults.LibraryEpisodeItem(
        modifier = Modifier
            .clickable(onClick = onEpisodeClick),
        text = { Text(text = storeEpisode.name, maxLines = 2) },
        overlineText = {
            val context = AmbientContext.current
            Text(text = storeEpisode.releaseDateTime.formatRelativeDisplay(context))
        },
        actionButtons = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlayButton(storeEpisode = storeEpisode)
                QueueButton(storeEpisode = storeEpisode)
            }
        },
        icon = {
            PodcastThumbnail(
                imageModel = storeEpisode.getArtworkUrl(),
                modifier = Modifier
                    .preferredSize(EpisodeDefaults.ThumbnailSize)
            )
        }
    )
}

@Composable
private fun PodcastEpisodeItemHeader(
    modifier: Modifier = Modifier,
    storeEpisode: StoreEpisode)
{
    EpisodeDefaults.PodcastEpisodeItemHeader(
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
            val context = AmbientContext.current
            Text(text = storeEpisode.releaseDateTime.formatRelativeDisplay(context), maxLines = 1)
        })
}

private object EpisodeDefaults {
    // List item related defaults.
    private val MinHeight = 88.dp

    // Icon related defaults.
    private val IconMinPaddedWidth = 40.dp
    private val IconLeftPadding = 16.dp
    private val IconThreeLineVerticalPadding = 16.dp

    // Default thumbnail size
    val ThumbnailSize = 56.dp

    // Small thumbnail size
    val ThumbnailSizeSmall = 40.dp

    // Content related defaults.
    private val ContentLeftPadding = 16.dp
    private val ContentRightPadding = 16.dp
    private val ContentInnerPadding = 8.dp
    private val ContentTopPadding = 16.dp
    private val ContentBottomPadding = 8.dp
    private val ActionsPadding = 4.dp

    @Composable
    private fun StoreItem(
        modifier: Modifier,
        storeEpisode: StoreEpisode,
        iconModifier: Modifier,
        index: Int? = null
    ) {
        StoreEpisodeItem(
            modifier = modifier.fillMaxWidth(),
            text = {
                if (index != null) {
                    Text(text = AnnotatedString.Builder()
                        .apply {
                            withStyle(SpanStyle(color = Color.Red)) {
                                append("${index}. ")
                            }
                            append(storeEpisode.name)
                        }.toAnnotatedString(),
                        maxLines = 2)
                } else {
                    Text(text = storeEpisode.name, maxLines = 2)
                }
            },
            podcastText = {
                Text(storeEpisode.podcastName, maxLines = 1)
            },
            icon = {
                PodcastThumbnail(
                    imageModel = storeEpisode.getArtworkUrl(),
                    modifier = iconModifier
                )
            },
            actionButtons = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlayButton(
                        storeEpisode = storeEpisode,
                    )
                    QueueButton(
                        storeEpisode = storeEpisode
                    )
                }
            }
        )
    }

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
    fun PodcastEpisodeItem(
        modifier: Modifier = Modifier,
        icon: @Composable () -> Unit,
        podcastText: @Composable () -> Unit,
        releasedTimeText: @Composable () -> Unit,
        descriptionText: @Composable (() -> Unit)? = null,
        actionButtons: @Composable (() -> Unit),
        text: @Composable () -> Unit)
    {

        val typography = MaterialTheme.typography

        val styledText = applyTextStyleCustom(typography.subtitle1, ContentAlpha.high, text)
        val styledPodcastText = applyTextStyleCustom(typography.body2, ContentAlpha.medium, podcastText)
        val styledReleasedTimeText = applyTextStyleCustom(typography.caption, ContentAlpha.medium, releasedTimeText)!!
        val styledTrailing = applyTextStyleCustom(typography.caption, ContentAlpha.high, actionButtons)
        val styledDescriptionText = applyTextStyleNullable(typography.subtitle1, ContentAlpha.high, descriptionText)

        Column(modifier
            .preferredHeightIn(min = MinHeight)
            .padding(start = ContentLeftPadding, end = ContentRightPadding, top = ContentTopPadding))
        {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = ContentInnerPadding))
            {
                Box(modifier = Modifier.preferredSize(32.dp)) {
                    icon()
                }

                Column(modifier = Modifier
                    .weight(1f)
                    .padding(start = ContentInnerPadding))
                {
                    styledPodcastText()
                    styledReleasedTimeText()
                }
            }

            styledText()
            if (styledDescriptionText != null)
                styledDescriptionText()

            actionButtons()
        }
    }

    @Composable
    fun EpisodeItem(
        modifier: Modifier = Modifier,
        overlineText: @Composable () -> Unit,
        descriptionText: @Composable (() -> Unit)? = null,
        actionButtons: @Composable (() -> Unit),
        text: @Composable () -> Unit)
    {
        val typography = MaterialTheme.typography

        val styledText = applyTextStyleCustom(typography.subtitle1.copy(fontWeight = FontWeight.Medium), ContentAlpha.high, text)
        val styledOverlineText = applyTextStyleCustom(typography.caption, ContentAlpha.medium, overlineText)
        val styledDescriptionText = applyTextStyleNullable(typography.body2, ContentAlpha.high, descriptionText)

        Column(modifier
            .fillMaxWidth()
            .preferredHeightIn(min = MinHeight)
            .padding(start = ContentLeftPadding, end = ContentRightPadding, top = ContentTopPadding))
        {
            styledOverlineText()
            styledText()
            if (styledDescriptionText != null)
                styledDescriptionText()

            actionButtons()
        }
    }

    @Composable
    fun PodcastEpisodeItemHeader(
        modifier: Modifier = Modifier,
        icon: @Composable () -> Unit,
        podcastText: @Composable () -> Unit,
        releasedTimeText: @Composable () -> Unit)
    {

        val typography = MaterialTheme.typography

        val styledPodcastText = applyTextStyleCustom(typography.body2, ContentAlpha.high, podcastText)
        val styledReleasedTimeText = applyTextStyleCustom(typography.caption, ContentAlpha.medium, releasedTimeText)!!

        Row(modifier = modifier
            .fillMaxWidth()
            .padding(bottom = ContentInnerPadding))
        {
            Box(modifier = Modifier.preferredSize(32.dp)) {
                icon()
            }

            Column(modifier = Modifier
                .weight(1f)
                .padding(start = ContentInnerPadding))
            {
                styledPodcastText()
                styledReleasedTimeText()
            }
        }
    }

    @Composable
    private fun StoreEpisodeItem(
        modifier: Modifier = Modifier,
        icon: @Composable () -> Unit,
        podcastText: @Composable () -> Unit,
        actionButtons: @Composable (() -> Unit),
        text: @Composable () -> Unit) {

        val typography = MaterialTheme.typography

        val styledText = applyTextStyleCustom(typography.subtitle1, ContentAlpha.high, text)
        val styledPodcastText = applyTextStyleCustom(typography.body2, ContentAlpha.medium, podcastText)

        val semanticsModifier = modifier.semantics(mergeDescendants = true) {}

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
                    styledText()
                    styledPodcastText()
                }
            }

            actionButtons()
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
        val styledOverlineText = applyTextStyleCustom(typography.caption, ContentAlpha.high, overlineText)!!

        val semanticsModifier = modifier.semantics(mergeDescendants = true) {}

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

