package com.caldeirasoft.outcast.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.ui.screen.podcastsettings.PodcastSettingsViewModel
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.toDp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlin.math.ln

@Composable
fun ReachableScaffold(
    headerRatioOrientation: Orientation = Orientation.Vertical,
    headerRatio: Float = 1/3f,
    itemContent: @Composable BoxWithConstraintsScope.(Int) -> Unit = {}
) {
    Scaffold {
        Box(modifier = Modifier
            .fillMaxSize()
            .semantics { testTag = "Store Directory screen" })
        {
            BoxWithConstraints {
                val screenHeight = constraints.maxHeight
                val screenWidth = constraints.maxWidth
                val headerHeight =
                    if (headerRatioOrientation == Orientation.Vertical)
                        (screenHeight * headerRatio).toInt()
                    else (screenWidth * headerRatio).toInt()

                itemContent(headerHeight)
            }
        }
    }
}

@Composable
fun ScaffoldWithLargeHeader(
    modifier: Modifier = Modifier,
    headerRatioOrientation: Orientation = Orientation.Vertical,
    headerRatio: Float = 1/3f,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    listState: LazyListState = rememberLazyListState(),
    topBar: @Composable () -> Unit = {},
    itemContent: @Composable BoxWithConstraintsScope.(Int) -> Unit = {}
) {
    val appBarAlpha = listState.topAppBarAlpha
    val backgroundColor: Color = Color.blendARGB(
        MaterialTheme.colors.surface.copy(alpha = 0f),
        MaterialTheme.colors.surface,
        appBarAlpha)

    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        snackbarHost = snackbarHost,
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .semantics { testTag = "Store Directory screen" })
        {
            BoxWithConstraints {
                val screenHeight = constraints.maxHeight
                val screenWidth = constraints.maxWidth
                val headerHeight =
                    if (headerRatioOrientation == Orientation.Vertical)
                        (screenHeight * headerRatio).toInt()
                    else (screenWidth * headerRatio).toInt()

                itemContent(headerHeight)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                topBar()
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ScaffoldWithLargeHeaderAndLazyColumn(
    modifier: Modifier = Modifier,
    title: String,
    headerRatioOrientation: Orientation = Orientation.Vertical,
    headerRatio: Float = 1/3f,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    listState: LazyListState = rememberLazyListState(),
    showTopBar: Boolean = true,
    navigateUp: () -> Unit = {},
    topBarActions: @Composable RowScope.() -> Unit = {},
    itemsContent: LazyListScope.() -> Unit = {}
) {
    ScaffoldWithLargeHeader(
        modifier = modifier,
        scaffoldState = scaffoldState,
        listState = listState,
        snackbarHost = snackbarHost,
        headerRatioOrientation = headerRatioOrientation,
        headerRatio = headerRatio,
        topBar = {
            if (showTopBar) {
                val appBarAlpha = listState.topAppBarAlpha
                val backgroundColor: Color = Color.blendARGB(
                    MaterialTheme.colors.surface.copy(alpha = 0f),
                    MaterialTheme.colors.surface,
                    appBarAlpha
                )
                Column(
                    modifier = Modifier
                ) {
                    TopAppBar(
                        modifier = Modifier,
                        title = {
                            AnimatedVisibility(
                                visible = (appBarAlpha == 1f),
                                enter = fadeIn(),
                                exit = fadeOut(),
                            ) {
                                Text(text = title)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = navigateUp) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = null)
                            }
                        },
                        actions = topBarActions,
                        backgroundColor = backgroundColor,
                        contentColor = MaterialTheme.colors.onSurface,
                        elevation = 0.dp
                    )

                    if (appBarAlpha == 1f)
                        Divider()
                }
            }
        }
    ) { headerHeight ->
        LazyColumn(state = listState) {
            // header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = headerHeight.toDp())
                ) {
                    Text(
                        text = title,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(top = 16.dp, bottom = 16.dp)
                            .padding(start = 16.dp, end = 16.dp),
                        style = typography.h4
                    )
                }
            }

            itemsContent()
        }
    }
}


@Composable
fun ReachableAppBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    expandedContent: @Composable BoxScope.() -> Unit =
        {
            val alphaLargeHeader = getExpandedHeaderAlpha(state, headerHeight)
            // large title
            Box(modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp
                )
                .align(Alignment.Center)
                .alpha(alphaLargeHeader)) {
                ProvideTextStyle(typography.h4, title)
            }
        },
    collapsedContent: @Composable BoxScope.() -> Unit =
        {
            val collapsedHeaderAlpha = getCollapsedHeaderAlpha(state, headerHeight)
            // top app bar
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    CompositionLocalProvider(LocalContentAlpha provides collapsedHeaderAlpha) {
                        title()
                    }
                },
                navigationIcon = navigationIcon,
                actions = actions,
                backgroundColor = Color.Transparent,
                elevation = if (state.firstVisibleItemIndex > 0) 1.dp else 0.dp
            )
        },
    state: LazyListState,
    headerHeight: Int)
{
    ReachableAppBar(expandedContent, collapsedContent, state, headerHeight)
}

@Composable
fun ReachableAppBar(
    expandedContent: @Composable BoxScope.() -> Unit = {},
    collapsedContent: @Composable BoxScope.() -> Unit = {},
    state: LazyListState,
    headerHeight: Int)
{
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(headerHeight.toDp()))
    {
        val scrollRatioHeaderHeight =
            if (headerHeight != 0)
                ((headerHeight - (state.firstVisibleItemIndex * headerHeight.toFloat() + state.firstVisibleItemScrollOffset)) / headerHeight)
                    .coerceAtLeast(0f)
            else 1f
        val minimumHeight = AppBarHeight
        val computedHeight =
            (scrollRatioHeaderHeight * headerHeight).toDp().coerceAtLeast(minimumHeight)
        val collapsedHeaderAlpha = getCollapsedHeaderAlpha(state, headerHeight)
        val backgroundColor: Color = Color.blendARGB(
            MaterialTheme.colors.surface.copy(alpha = 0f),
            MaterialTheme.colors.surface,
            collapsedHeaderAlpha)

        Box(modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding(bottom = false)
            .height(computedHeight)) {

            // expanded content
            this.expandedContent()

            // collapsed content
            Box(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart))
            {
                this.collapsedContent()
            }
        }

    }
}

@Composable
fun CustomTopAppBar(
    expandedContent: @Composable BoxScope.() -> Unit = {},
    collapsedContent: @Composable BoxScope.() -> Unit = {},
    state: LazyListState,
    headerHeight: Int,
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(headerHeight.toDp()))
    {
        val scrollRatioHeaderHeight =
            if (headerHeight != 0)
                ((headerHeight - (state.firstVisibleItemIndex * headerHeight.toFloat() + state.firstVisibleItemScrollOffset)) / headerHeight)
                    .coerceAtLeast(0f)
            else 1f
        val minimumHeight = AppBarHeight
        val computedHeight =
            (scrollRatioHeaderHeight * headerHeight).toDp().coerceAtLeast(minimumHeight)
        val collapsedHeaderAlpha = getCollapsedHeaderAlpha(state, headerHeight)
        val backgroundColor: Color = Color.blendARGB(
            MaterialTheme.colors.surface.copy(alpha = 0f),
            MaterialTheme.colors.surface,
            collapsedHeaderAlpha)

        Box(modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .statusBarsPadding()
            .navigationBarsPadding(bottom = false)
            .height(computedHeight)) {

            // expanded content
            this.expandedContent()

            // collapsed content
            Box(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart))
            {
                this.collapsedContent()
            }
        }

    }
}

@ExperimentalAnimationApi
@Composable
fun TopHeaderExpanded(
    state: LazyListState,
    headerHeight: Int,
    expandedContent: @Composable BoxScope.() -> Unit = {},
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(headerHeight.toDp()))
    {
        val scrollRatioHeaderHeight =
            if (headerHeight != 0)
                ((headerHeight - (state.firstVisibleItemIndex * headerHeight.toFloat() + state.firstVisibleItemScrollOffset)) / headerHeight)
                    .coerceAtLeast(0f)
            else 1f
        val minimumHeight = 56.dp
        val computedHeight = (scrollRatioHeaderHeight * headerHeight).toDp().coerceAtLeast(minimumHeight)
        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = computedHeight)
            .height(computedHeight)
            .background(MaterialTheme.colors.background)) {

            // expanded content
            this.expandedContent()
        }
    }
}

val LazyListState.headerScrollRatio: Float
    get() =
        when {
            layoutInfo.visibleItemsInfo.isEmpty() -> 1f
            firstVisibleItemIndex > 0 -> 0f
            else -> {
                val headerHeight = layoutInfo.visibleItemsInfo[0].size
                if (headerHeight != 0)
                    ((headerHeight - firstVisibleItemScrollOffset).toFloat() / headerHeight)
                        .coerceAtLeast(0f)
                else 1f
            }
        }

val LazyListState.expandedHeaderAlpha: Float
    get() = (ln(x = headerScrollRatio.toDouble()) + 1).toFloat().coerceIn(0f, 1f)

val LazyListState.topAppBarAlpha: Float
    get() = (1.3 * ln(1 - headerScrollRatio.toDouble()) + 1.9).toFloat().coerceIn(0f, 1f)

fun getScrollRatioHeaderHeight(state: LazyListState, headerHeight: Int = 0): Float {
    return if (headerHeight != 0)
        ((headerHeight - (state.firstVisibleItemIndex * headerHeight.toFloat() + state.firstVisibleItemScrollOffset)) / headerHeight)
            .coerceAtLeast(0f)
    else 1f
}

fun getExpandedHeaderAlpha(state: LazyListState, headerHeight: Int = 0) =
    (ln(x = getScrollRatioHeaderHeight(state, headerHeight).toDouble()) + 1).toFloat()
        .coerceIn(0f, 1f)

fun getCollapsedHeaderAlpha(state: LazyListState, headerHeight: Int = 0) =
    (1.3 * ln(1 - getScrollRatioHeaderHeight(state, headerHeight).toDouble()) + 1.9).toFloat()
        .coerceIn(0f, 1f)

val AppBarHeight = 56.dp