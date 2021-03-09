package com.caldeirasoft.outcast.ui.screen.episode

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsState
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.ui.components.OverflowText
import com.caldeirasoft.outcast.ui.components.PlayButton
import com.caldeirasoft.outcast.ui.components.PodcastThumbnail
import com.caldeirasoft.outcast.ui.components.QueueButton
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetState
import com.caldeirasoft.outcast.ui.components.bottomsheet.ModalBottomSheetContent
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.util.DateFormatter.formatRelativeDisplay
import com.caldeirasoft.outcast.ui.util.mavericksViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

suspend fun openEpisodeDialog(drawerState: ModalBottomSheetState, drawerContent: ModalBottomSheetContent, storeEpisode: StoreEpisode) {
    drawerContent.updateContent {
        EpisodeDialog(
            episode = storeEpisode,
            navigateTo = { }
        )
    }
    drawerState.show()
}

@ExperimentalCoroutinesApi
@Composable
fun EpisodeDialog(
    episode: StoreEpisode,
    navigateTo: (Screen) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState(0)
    val drawerState = LocalBottomSheetState.current
    val viewModel: EpisodeViewModel = mavericksViewModel(initialArgument = episode )
    val state by viewModel.collectAsState()
    val storeEpisode = requireNotNull(state.storeEpisode.invoke())

    Column()
    {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.store_tab_categories))
            },
            navigationIcon = {
                IconButton(onClick = {
                    coroutineScope.launch {
                        drawerState.hide()
                    }
                }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                }
            },
            backgroundColor = Color . Transparent,
            elevation = if (scrollState.value > 0) 1.dp else 0.dp,
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)) {
            // thumbnail + podcast title + release date
            Row(modifier = Modifier
                .fillMaxWidth()
                )
            {
                // thumbnail
                Box(modifier = Modifier.size(64.dp)) {
                    PodcastThumbnail(
                        imageModel = storeEpisode.getArtworkUrl(),
                        modifier = Modifier
                            .size(64.dp)
                    )
                }

                // podcast name + release date
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp))
                {
                    Text(text = storeEpisode.podcastName, maxLines = 2)
                    val context = LocalContext.current
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

