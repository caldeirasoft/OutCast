package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.theme.colors
import com.skydoves.landscapist.coil.CoilImage


@Composable
fun PodcastListItemIndexed(
    modifier: Modifier = Modifier,
    storePodcast: StorePodcast,
    index: Int,
    nameMaxLines: Int = 2,
    iconModifier: Modifier = Modifier.size(PodcastDefaults.ThumbnailSize)
) {
    ListItem(
        modifier = modifier.fillMaxWidth(),
        text = {
            Text(
                AnnotatedString.Builder().apply {
                    withStyle(SpanStyle(color = Color.Red)) {
                        append("${index}. ")
                    }
                    append(storePodcast.name)
                }.toAnnotatedString(),
                maxLines = nameMaxLines
            )
        },
        secondaryText = { Text(storePodcast.artistName) },
        icon = {
            PodcastThumbnail(
                imageModel = storePodcast.getArtworkUrl(),
                modifier = iconModifier
            )
        }
    )
}

@Composable
fun SmallPodcastListItemIndexed(
    modifier: Modifier = Modifier,
    storePodcast: StorePodcast,
    index: Int
) = PodcastListItemIndexed(
    modifier = modifier,
    storePodcast = storePodcast,
    index = index,
    nameMaxLines = 1,
    iconModifier = Modifier.size(PodcastDefaults.SmallThumbnailSize))


@Composable
fun PodcastThumbnail(
    modifier: Modifier = Modifier,
    imageModel: Any,
    backgroundColor: Color = MaterialTheme.colors.surface,
) {
    Card(
        backgroundColor = colors[1],
        shape = RoundedCornerShape(8.dp)
    ) {
        CoilImage(
            imageModel = imageModel,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}

@Composable
fun PodcastGridItem(
    modifier: Modifier = Modifier,
    podcast: StorePodcast,
    index: Int? = null,
)
{
    Column(modifier = modifier
        .padding(horizontal = 8.dp)) {
        Card(
            backgroundColor = colors[1],
            shape = RoundedCornerShape(8.dp)
        )
        {
            CoilImage(
                imageModel = podcast.getArtworkUrl(),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f))
        }
        Text(
            text = with(AnnotatedString.Builder()) {
                if (index != null) {
                    pushStyle(SpanStyle(color = Color.Red))
                    append("${index}. ")
                    pop()
                }
                append(podcast.name)
                toAnnotatedString()
            },
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.body2
        )
        Text(
            podcast.artistName,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption
        )
    }
}

object PodcastDefaults {
    /**
     * Default thumbnail size
     */
    val ThumbnailSize = 56.dp

    /**
     * Small thumbnail size
     */
    val SmallThumbnailSize = 40.dp
}