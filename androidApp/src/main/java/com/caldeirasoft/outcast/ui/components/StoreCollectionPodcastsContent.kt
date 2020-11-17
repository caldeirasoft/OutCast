package com.caldeirasoft.outcast.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.domain.models.StoreCollectionPodcasts
import com.caldeirasoft.outcast.domain.models.StorePodcast
import com.caldeirasoft.outcast.ui.theme.colors

@Composable
fun StoreCollectionPodcastsContent(
    storeCollection: StoreCollectionPodcasts,
    navigateToStoreEntry: (String) -> Unit
) {
    Text(storeCollection.label, modifier = Modifier.padding(horizontal = 16.dp))
    if (storeCollection.items.isEmpty())
        Row(
            modifier = Modifier
                .padding(8.dp)
                .preferredHeight(100.dp)
                .fillMaxWidth()
        ) {
            (1..7).forEach { podcast ->
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
                .padding(8.dp)
                .fillMaxWidth()
        ) { podcast ->
            StorePodcastGridItem(
                podcast = podcast,
                navigateToStoreEntry = navigateToStoreEntry)
        }
}
