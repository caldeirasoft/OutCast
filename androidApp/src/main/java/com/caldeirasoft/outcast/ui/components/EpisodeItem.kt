package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode


@Composable
fun StoreEpisodeItemFromCharts(
    storeEpisode: StoreEpisode,
    index: Int,
    onEpisodeClick: () -> Unit,
    onThumbnailClick: () -> Unit
) = EpisodeDefaults.StoreItemIndexed(
    modifier = Modifier
        .clickable(onClick = onEpisodeClick),
    storeEpisode = storeEpisode,
    index = index,
    iconModifier = Modifier
        .preferredSize(EpisodeDefaults.ThumbnailSize)
        .clickable(onClick = onThumbnailClick))


@Composable
fun StoreEpisodeCardItem(
    modifier: Modifier = Modifier,
    storeEpisode: StoreEpisode,
    iconModifier: Modifier = Modifier.preferredSize(EpisodeDefaults.ThumbnailSize),
    onEpisodeClick: () -> Unit,
    onThumbnailClick: () -> Unit,
) = EpisodeDefaults.CardItem(
    modifier = Modifier
        .clickable(onClick = onEpisodeClick),
) {
    EpisodeDefaults.StoreItem(
        modifier = Modifier,
        storeEpisode = storeEpisode,
        iconModifier = Modifier
            .preferredSize(EpisodeDefaults.ThumbnailSize)
            .clickable(onClick = onThumbnailClick))
}

@Composable
fun StoreEpisodeCardItemFromCharts(
    modifier: Modifier = Modifier,
    storeEpisode: StoreEpisode,
    index: Int,
    onEpisodeClick: () -> Unit,
    onThumbnailClick: () -> Unit,
) = EpisodeDefaults.CardItem(
    modifier = Modifier
        .clickable(onClick = onEpisodeClick),
)  {
    EpisodeDefaults.StoreItemIndexed(
        modifier = Modifier,
        storeEpisode = storeEpisode,
        index = index,
        iconModifier = Modifier
            .preferredSize(EpisodeDefaults.ThumbnailSize)
            .clickable(onClick = onThumbnailClick))
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

    // Content related defaults.
    private val ContentLeftPadding = 16.dp
    private val ContentRightPadding = 16.dp
    private val ContentInnerPadding = 4.dp
    private val ContentTopPadding = 16.dp
    private val ContentBottomPadding = 8.dp

    @Composable
    fun StoreItem(
        modifier: Modifier,
        storeEpisode: StoreEpisode,
        iconModifier: Modifier
    ) {
        EpisodeItem(
            modifier = modifier.fillMaxWidth(),
            text = { Text(text = storeEpisode.name) },
            secondaryText = { Text(storeEpisode.podcastName, maxLines = 1) },
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
    fun StoreItemIndexed(
        modifier: Modifier,
        storeEpisode: StoreEpisode,
        index: Int,
        iconModifier: Modifier
    ) {
        EpisodeItem(
            modifier = modifier.fillMaxWidth(),
            text = {
                Text(
                    AnnotatedString.Builder().apply {
                        withStyle(SpanStyle(color = Color.Red)) {
                            append("${index}. ")
                        }
                        append(storeEpisode.name)
                    }.toAnnotatedString(),
                    maxLines = 1
                )
            },
            secondaryText = { Text(storeEpisode.podcastName, maxLines = 1) },
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
        Box(modifier = Modifier.padding(start = 16.dp)) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                border = ButtonDefaults.outlinedBorder,
                elevation = 0.dp,
                modifier = modifier
            ) {
                itemContent()
            }
        }
    }

    @Composable
    private fun EpisodeItem(
        modifier: Modifier = Modifier,
        icon: @Composable (() -> Unit)? = null,
        secondaryText: @Composable (() -> Unit)? = null,
        overlineText: @Composable (() -> Unit)? = null,
        descriptionText: @Composable (() -> Unit)? = null,
        actionButtons: @Composable (() -> Unit),
        text: @Composable () -> Unit) {

        val typography = MaterialTheme.typography

        val styledText = applyTextStyle(typography.subtitle1, ContentAlpha.high, text)!!
        val styledSecondaryText = applyTextStyle(typography.body2, ContentAlpha.medium, secondaryText)
        val styledOverlineText = applyTextStyle(typography.overline, ContentAlpha.high, overlineText)
        val styledTrailing = applyTextStyle(typography.caption, ContentAlpha.high, actionButtons)
        val styledDescriptionText = applyTextStyle(typography.subtitle1, ContentAlpha.high, descriptionText)

        val semanticsModifier = modifier.semantics(mergeDescendants = true) {}
        ConstraintLayout(modifier.preferredHeightIn(min = EpisodeDefaults.MinHeight)) {
            val (thumbnail, info) = createRefs()
            Box(modifier = Modifier.constrainAs(thumbnail) {
                linkTo(top = parent.top, bottom = parent.bottom)
                linkTo(start = parent.start, end = info.start)
                width = Dimension.preferredWrapContent
                height = Dimension.fillToConstraints
            }) {
                if (icon != null) {
                    val minSize = EpisodeDefaults.IconLeftPadding + EpisodeDefaults.IconMinPaddedWidth
                    Box(
                        Modifier
                            .preferredSizeIn(minWidth = minSize, minHeight = minSize)
                            .padding(
                                start = EpisodeDefaults.IconLeftPadding,
                                top = EpisodeDefaults.ContentTopPadding,
                            ),
                        contentAlignment = Alignment.TopStart
                    ) {
                        icon()
                    }
                }
            }

            Column(
                modifier = Modifier
                    .constrainAs(info) {
                        linkTo(top = thumbnail.top, bottom = thumbnail.bottom)
                        linkTo(start = thumbnail.end, end = parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .padding(
                        start = EpisodeDefaults.ContentLeftPadding,
                        end = EpisodeDefaults.ContentRightPadding,
                        top = EpisodeDefaults.ContentTopPadding,
                        bottom = EpisodeDefaults.ContentBottomPadding,
                    )
            ) {
                if (styledOverlineText != null) styledOverlineText()
                styledText()
                if (styledSecondaryText != null) styledSecondaryText()
                if (styledDescriptionText != null) {
                    Box(modifier = Modifier
                        .padding(top = EpisodeDefaults.ContentInnerPadding))
                    {
                        styledDescriptionText()
                    }
                }
                Box(modifier = Modifier)
                {
                    actionButtons()
                }
            }
        }
    }
}

private fun applyTextStyle(
    textStyle: TextStyle,
    contentAlpha: Float,
    icon: @Composable (() -> Unit)?
): @Composable (() -> Unit)? {
    if (icon == null) return null
    return {
        Providers(AmbientContentAlpha provides contentAlpha) {
            ProvideTextStyle(textStyle, icon)
        }
    }
}