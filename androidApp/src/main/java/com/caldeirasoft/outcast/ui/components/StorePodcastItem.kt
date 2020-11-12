package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.caldeirasoft.outcast.ui.theme.OutCastTheme
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun StorePodcastItem(
    podcastTitle: String,
    podcastArtist: String,
    podcastImageUrl: String,
    modifier: Modifier = Modifier
)
{
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = {})) {
        Column(modifier) {
            Box(Modifier.fillMaxWidth())
            {
                CoilImage(
                    imageModel = podcastImageUrl,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(80.dp))
            }
            Text(text = podcastTitle, style = MaterialTheme.typography.body2,
            maxLines = 2, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp).fillMaxWidth())
        }
    }
}

