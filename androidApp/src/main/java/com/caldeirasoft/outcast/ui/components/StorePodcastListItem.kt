package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.caldeirasoft.outcast.domain.models.StorePodcast
import com.caldeirasoft.outcast.ui.theme.colors
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun StorePodcastListItem(
    podcast: StorePodcast,
    navigateToStoreEntry: (String) -> Unit
)
{
    Row(modifier = Modifier
        .padding(8.dp)
        .preferredHeight(100.dp)
        .fillMaxWidth()
        .clickable(onClick = { navigateToStoreEntry(podcast.url) })) {
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
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            androidx.compose.material.Text(
                podcast.name,
                modifier = Modifier.fillMaxWidth(),
                overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.body2
            )
            androidx.compose.material.Text(
                podcast.artistName,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption
            )
        }
    }
}

