package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.models.StorePodcast
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun StorePodcastListItem(podcast: StorePodcast)
{
    val actions = ActionsAmbient.current

    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = { actions.navigateToStoreEntry(podcast.url) })) {
        ListItem(
            text = { Text(text = podcast.name) },
            secondaryText = { Text(text = podcast.artistName) },
            icon = {
                CoilImage(
                    imageModel = podcast.getArtworkUrl(),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .preferredSize(56.dp))
            }
        )
        /*
        Card(
            backgroundColor = colors[1],
            shape = RoundedCornerShape(8.dp)
        )
        {
            CoilImage(
                imageModel = podcast.getArtworkUrl(),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .preferredSize(70.dp))
        }
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            androidx.compose.material.Text(
                podcast.name,
                modifier = Modifier.fillMaxWidth(),
                overflow = TextOverflow.Ellipsis, maxLines = 2,
                style = MaterialTheme.typography.body2
            )
            androidx.compose.material.Text(
                podcast.artistName,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption
            )
        }*/
    }
}

