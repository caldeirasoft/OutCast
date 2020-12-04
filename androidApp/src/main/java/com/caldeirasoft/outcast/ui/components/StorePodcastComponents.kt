package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.models.StorePodcast
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.theme.colors
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun StorePodcastListItem(podcast: StorePodcast)
{
    val actions = ActionsAmbient.current

    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = { actions.navigateToStorePodcast(podcast.url) })) {
        ListItem(
            text = { Text(text = podcast.name) },
            secondaryText = { Text(text = podcast.artistName) },
            icon = {
                Card(
                    backgroundColor = colors[1],
                    shape = RoundedCornerShape(8.dp)
                ) {
                    CoilImage(
                        imageModel = podcast.getArtworkUrl(),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .preferredSize(56.dp)
                    )
                }
            }
        )
    }
}

@Composable
fun StorePodcastListItemIndexed(podcast: StorePodcast, index: Int)
{
    val actions = ActionsAmbient.current

    Row(modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { actions.navigateToStorePodcast(podcast.url) })) {
        ListItem(
                text = { Text(text = podcast.name) },
                secondaryText = { Text(text = podcast.artistName) },
                icon = {
                    Row(horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.preferredWidth(90.dp)) {
                        Text(index.toString(),
                            style = MaterialTheme.typography.body2)
                        Card(
                                backgroundColor = colors[1],
                                shape = RoundedCornerShape(8.dp)
                        ) {
                            CoilImage(
                                    imageModel = podcast.getArtworkUrl(),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                            .preferredSize(56.dp)
                            )
                        }
                    }
                }
        )
    }
}

@Composable
fun StorePodcastGridItem(podcast: StorePodcast)
{
    val actions = ActionsAmbient.current
    Column(modifier = Modifier
        .padding(horizontal = 8.dp)
        .preferredWidth(100.dp)
        .clickable(onClick = { actions.navigateToStorePodcast(podcast.url) })) {
        Card(
            backgroundColor = colors[1],
            shape = RoundedCornerShape(8.dp)
        )
        {
            CoilImage(
                imageModel = podcast.getArtworkUrl(),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .preferredSize(100.dp))
        }
        Text(
            podcast.name,
            modifier = Modifier.width(100.dp),
            overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.body2
        )
        Text(
            podcast.artistName,
            modifier = Modifier.width(100.dp),
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption
        )
    }
}
