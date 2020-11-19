package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.caldeirasoft.outcast.domain.models.StoreCollectionPodcasts
import com.caldeirasoft.outcast.domain.models.StorePodcast
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.theme.colors

@Composable
fun StoreCollectionPodcastsContent(storeCollection: StoreCollectionPodcasts) {
    Column(modifier = Modifier.padding(
        horizontal = 8.dp, vertical = 16.dp
    )) {
        StoreHeadingSectionWithLink(
            title = storeCollection.label,
            url = storeCollection.url ?: ""
        )
        Spacer(modifier = Modifier.preferredHeight(8.dp))
        if (storeCollection.items.isEmpty())
            Row(
                modifier = Modifier
                    .preferredHeight(100.dp)
                    .fillMaxWidth()
            ) {
                (1..2).forEach { podcast ->
                    Card(
                        backgroundColor = colors[0],
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(horizontal = 8.dp))
                    {
                        Spacer(modifier = Modifier.preferredSize(100.dp))
                    }
                }
            }
        else
            LazyRowFor(
                items = storeCollection.items.filterIsInstance<StorePodcast>(),
                modifier = Modifier
                    .fillMaxWidth()
            ) { podcast ->
                StorePodcastGridItem(podcast = podcast)
            }

    }
}

@Preview
@Composable
fun previewStoreCollectionPodcastContent() {
    val collection = remember { StoreCollectionPodcasts("Nouveautés et tendances", itemsIds = mutableListOf()) }
    StoreCollectionPodcastsContent(storeCollection = collection)
}
