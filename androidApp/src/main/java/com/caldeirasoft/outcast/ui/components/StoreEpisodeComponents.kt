package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.models.StoreEpisode
import com.caldeirasoft.outcast.domain.models.StorePodcast
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.theme.colors
import com.skydoves.landscapist.coil.CoilImage


@Composable
fun StoreEpisodeListItem(episode: StoreEpisode)
{
    val actions = ActionsAmbient.current

    Row(modifier = Modifier
        .fillMaxWidth()
        //.clickable(onClick = { actions.navigateToStoreCollection(podcast.name, podcast.url) })
    ) {
        ListItem(
            text = { Text(text = episode.name) },
            secondaryText = { Text(text = episode.podcastName) },
            icon = {
                Card(
                    backgroundColor = colors[1],
                    shape = RoundedCornerShape(8.dp)
                ) {
                    CoilImage(
                        imageModel = episode.getArtworkUrl(),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .preferredSize(56.dp)
                    )
                }
            }
        )
    }
}