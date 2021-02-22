package com.caldeirasoft.outcast.ui.screen.episode

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.ui.components.OverflowText
import com.caldeirasoft.outcast.ui.components.PlayButton
import com.caldeirasoft.outcast.ui.components.PodcastThumbnail
import com.caldeirasoft.outcast.ui.components.QueueButton
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetContent
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetState
import com.caldeirasoft.outcast.ui.components.bottomsheet.ModalBottomSheetContent
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.util.DateFormatter.formatRelativeDisplay
import com.caldeirasoft.outcast.ui.util.viewModelProviderFactoryOf
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Composable
fun openEpisodeDialog(storeEpisode: StoreEpisode) {
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current
    drawerContent.updateContent {
        EpisodeDialog(
            storeEpisode = storeEpisode,
            navigateTo = { }
        )
    }
    drawerState.show()
}

fun openEpisodeDialog(drawerState: ModalBottomSheetState, drawerContent: ModalBottomSheetContent, storeEpisode: StoreEpisode) {
    drawerContent.updateContent {
        EpisodeDialog(
            storeEpisode = storeEpisode,
            navigateTo = { }
        )
    }
    drawerState.show()
}

@ExperimentalCoroutinesApi
@Composable
fun EpisodeDialog(
    storeEpisode: StoreEpisode,
    navigateTo: (Screen) -> Unit,
) {
    val scrollState = rememberScrollState(0f)
    val drawerState = LocalBottomSheetState.current
    val viewModel: EpisodeViewModel = viewModel(
        key = storeEpisode.id.toString(),
        factory = viewModelProviderFactoryOf { EpisodeViewModel(storeEpisode) }
    )
    val viewState by viewModel.state.collectAsState()
    val storeEpisode = viewState.storeEpisode

    Column()
    {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.store_tab_categories))
            },
            navigationIcon = {
                IconButton(onClick = { drawerState.hide() }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                }
            },
            backgroundColor = Color . Transparent,
            elevation = if (scrollState.value > 0) 1.dp else 0.dp,
        )

        ScrollableColumn(
            scrollState = scrollState,
            modifier = Modifier.padding(horizontal = 16.dp)) {
            // thumbnail + podcast title + release date
            Row(modifier = Modifier
                .fillMaxWidth()
                )
            {
                // thumbnail
                Box(modifier = Modifier.preferredSize(64.dp)) {
                    PodcastThumbnail(
                        imageModel = storeEpisode.getArtworkUrl(),
                        modifier = Modifier
                            .preferredSize(64.dp)
                    )
                }

                // podcast name + release date
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp))
                {
                    Text(text = storeEpisode.podcastName, maxLines = 2)
                    val context = AmbientContext.current
                    Text(text = storeEpisode.releaseDateTime.formatRelativeDisplay(context))
                }
            }
            // episode title
            Text(text = storeEpisode.name)

            // actions buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlayButton(storeEpisode = storeEpisode)
                QueueButton(storeEpisode = storeEpisode)
            }

            // description
            // description if present
            storeEpisode.description?.let { description ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                ) {
                    OverflowText(text = description,
                        overflow = TextOverflow.Clip,
                        textAlign = TextAlign.Justify,
                        maxLines = 5)
                }

            }
        }
    }
}

