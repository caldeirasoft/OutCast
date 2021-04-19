package com.caldeirasoft.outcast.ui.screen.library

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.presentation.viewmodel.LibraryViewModel
import com.caldeirasoft.outcast.ui.components.PodcastGridItem
import com.caldeirasoft.outcast.ui.components.ReachableAppBar
import com.caldeirasoft.outcast.ui.components.ReachableScaffold
import com.caldeirasoft.outcast.ui.components.gridItems
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.util.px
import com.caldeirasoft.outcast.ui.util.toDp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun LibraryScreen(
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: LibraryViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()

    LibraryScreen(
        state = state,
        navigateTo = navigateTo,
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun LibraryScreen(
    state: LibraryViewState,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val listState = rememberLazyListState(0)

    ReachableScaffold { headerHeight ->
        val spacerHeight = headerHeight - 56.px

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)) {

            item {
                Spacer(modifier = Modifier.height(spacerHeight.toDp()))
            }

            gridItems(
                items = state.podcasts,
                contentPadding = PaddingValues(16.dp),
                horizontalInnerPadding = 8.dp,
                verticalInnerPadding = 8.dp,
                columns = 3
            ) { item ->
                PodcastGridItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            navigateTo(Screen.PodcastScreen(item))
                        }),
                    podcast = item
                )
            }
        }

        ReachableAppBar(
            title = { Text(text = stringResource(id = R.string.screen_library)) },
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(Icons.Filled.ArrowBack,
                        contentDescription = null,)
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Filled.Search,
                        contentDescription = null,)
                }
            },
            state = listState,
            headerHeight = headerHeight)
    }
}
