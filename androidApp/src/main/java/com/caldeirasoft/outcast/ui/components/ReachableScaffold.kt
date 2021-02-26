package com.caldeirasoft.outcast.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.toDp
import kotlin.math.log10

@ExperimentalAnimationApi
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

@ExperimentalAnimationApi
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
                    end = 16.dp)
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

@ExperimentalAnimationApi
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
        val minimumHeight = 56.dp
        val computedHeight = (scrollRatioHeaderHeight * headerHeight).toDp().coerceAtLeast(minimumHeight)
        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = computedHeight)
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
    expandedContent: @Composable BoxScope.() -> Unit = {})
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

fun getScrollRatioHeaderHeight(state: LazyListState, headerHeight: Int) =
    if (headerHeight != 0)
        ((headerHeight - (state.firstVisibleItemIndex * headerHeight.toFloat() + state.firstVisibleItemScrollOffset)) / headerHeight)
            .coerceAtLeast(0f)
    else 1f

fun getExpandedHeaderAlpha(state: LazyListState, headerHeight: Int) =
    (3 * log10(getScrollRatioHeaderHeight(state, headerHeight).toDouble()) + 1).toFloat().coerceIn(0f, 1f)

fun getCollapsedHeaderAlpha(state: LazyListState, headerHeight: Int) =
    (3 * log10(1-getScrollRatioHeaderHeight(state, headerHeight).toDouble()) + 1).toFloat().coerceIn(0f, 1f)

