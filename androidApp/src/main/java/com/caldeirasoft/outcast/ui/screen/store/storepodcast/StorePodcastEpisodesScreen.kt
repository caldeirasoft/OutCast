package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcastPage
import com.caldeirasoft.outcast.domain.util.Log_D
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onLoading
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onSuccess
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.navigation.AmbientBottomDrawerContent
import com.caldeirasoft.outcast.ui.navigation.AmbientBottomDrawerState
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.openEpisodeDialog
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.util.*
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun StorePodcastEpisodesScreen(
    storePodcast: StorePodcast,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: StorePodcastViewModel = viewModel(
        key = storePodcast.url,
        factory = viewModelProviderFactoryOf { StorePodcastViewModel(storePodcast) }
    )
    val viewState by viewModel.state.collectAsState()

    StorePodcastEpisodesScreen(
        viewState = viewState,
        navigateTo = navigateTo,
        navigateBack = navigateBack
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StorePodcastEpisodesScreen(
    viewState: StorePodcastViewModel.State,
    navigateTo: (Screen) -> Unit,
    navigateBack: () ->
    Unit,
) {
    val drawerState = AmbientBottomDrawerState.current
    val drawerContent = AmbientBottomDrawerContent.current

    val listState = rememberLazyListState(1)

    ReachableScaffold { headerHeight ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()) {

            item {
                StorePodcastExpandedHeader(
                    viewState = viewState,
                    listState = listState,
                    headerHeight = headerHeight
                )
            }

            viewState.storeResourceData
                .onLoading {
                    item {
                        ShimmerStoreCollectionsList()
                    }
                }
                .onSuccess<StorePodcastPage> { it ->
                    // recent episodes
                    item {
                        StoreHeadingSection(title = "All episodes")
                    }
                    items(it.episodes) {
                        EpisodeItem(
                            storeEpisode = it,
                            onEpisodeClick = { openEpisodeDialog(drawerState, drawerContent, it) })

                        Divider()
                    }

                    item {
                        // bottom app bar spacer
                        Spacer(modifier = Modifier.height(56.dp))
                    }
                }
        }

        ReachableAppBar(
            collapsedContent = {
                StorePodcastCollapsedHeader(
                    viewState = viewState,
                    listState = listState,
                    headerHeight = headerHeight,
                    navigateUp = navigateBack)
            },
            state = listState,
            headerHeight = headerHeight)
    }
}
