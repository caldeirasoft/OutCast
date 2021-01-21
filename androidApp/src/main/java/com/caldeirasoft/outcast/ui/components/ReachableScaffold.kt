package com.caldeirasoft.outcast.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.layout.WithConstraintsScope
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.px

@ExperimentalAnimationApi
@Composable
fun ReachableScaffoldWithLazyColumn(
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    headerContent: @Composable (LazyListState, Int) -> Unit = { listState, headerHeight ->
        ReachableHeader(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            state = listState,
            headerHeight = headerHeight)
    },
    listContent: LazyListScope.() -> Unit,
) {
    Scaffold {
        Box(modifier = Modifier
            .fillMaxSize()
            .semantics { testTag = "Store Directory screen" })
        {
            WithConstraints {
                val screenHeight = constraints.maxHeight
                val headerHeight = screenHeight / 3
                val spacerHeight = headerHeight - 56.px

                val listState = rememberLazyListState(0)

                LazyColumn(state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 56.dp)) {
                    item {
                        with(AmbientDensity.current) {
                            Spacer(modifier = Modifier.height(spacerHeight.toDp()))
                        }
                    }

                    listContent()
                }

                headerContent(listState, headerHeight)
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun ReachableScaffold(
    itemContent: @Composable WithConstraintsScope.(Int) -> Unit = {},
) {
    Scaffold {
        Box(modifier = Modifier
            .fillMaxSize()
            .semantics { testTag = "Store Directory screen" })
        {
            WithConstraints {
                val screenHeight = constraints.maxHeight
                val headerHeight = screenHeight / 3

                itemContent(headerHeight)
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun ReachableHeader(
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    state: LazyListState,
    headerHeight: Int)
{
    with(AmbientDensity.current) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight.toDp()))
        {
            val scrollAlpha =
                if (headerHeight != 0)
                    ((headerHeight - (state.firstVisibleItemIndex * headerHeight.toFloat() + state.firstVisibleItemScrollOffset)) / headerHeight)
                        .coerceAtLeast(0f)
                else 1f
            val minimumHeight = 56.dp
            val computedHeight = (scrollAlpha * headerHeight).toDp().coerceAtLeast(minimumHeight)
            Box(modifier = Modifier
                .fillMaxWidth()
                .preferredHeightIn(max = computedHeight)
                .height(computedHeight)
                .background(MaterialTheme.colors.background)) {

                // large title
                Box(modifier = Modifier
                    .align(Alignment.Center)
                    .alpha(scrollAlpha)) {
                    ProvideTextStyle(typography.h4, title)
                }

                // top app bar
                TopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart),
                    title = {
                        Providers(AmbientContentAlpha provides (1 - scrollAlpha)) {
                            title()
                        }
                    },
                    navigationIcon = navigationIcon,
                    actions = actions,
                    backgroundColor = Color.Transparent,
                    elevation = if (state.firstVisibleItemIndex > 0) 1.dp else 0.dp
                )
            }

        }
    }
}
