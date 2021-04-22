package com.caldeirasoft.outcast.ui.screen.store.topcharts

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.store.topchartsection.TopChartEpisodeScreen
import com.caldeirasoft.outcast.ui.screen.store.topchartsection.TopChartPodcastScreen
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch


@OptIn(ExperimentalPagerApi::class)
@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun TopChartsScreen(
    storeItemType: StoreItemType = StoreItemType.PODCAST,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    // Remember a PagerState with our tab count
    val pagerState = rememberPagerState(pageCount = 2, initialPage = storeItemType.ordinal)

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(bottom = false),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.store_tab_charts))
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = { },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        }
    ) {
        TopChartsTabs(
            coroutineScope = coroutineScope,
            pagerState = pagerState,
            navigateTo = navigateTo)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TopChartsTabs(
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
    navigateTo: (Screen) -> Unit,
) {

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = Color.Transparent,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        )
        {
            StoreItemType.values().forEachIndexed { index, tab ->
                Tab(
                    selected = (index == pagerState.currentPage),
                    onClick = {
                        // Animate to the selected page when clicked
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(tab.ordinal)
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(id = when (tab) {
                                StoreItemType.PODCAST -> R.string.store_podcasts
                                StoreItemType.EPISODE -> R.string.store_episodes
                            }),
                            style = MaterialTheme.typography.body2)
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val itemType = StoreItemType.values()[page]
            Box(modifier = Modifier.fillMaxSize()) {
                when (itemType) {
                    StoreItemType.PODCAST -> TopChartPodcastScreen(navigateTo = navigateTo)
                    StoreItemType.EPISODE -> TopChartEpisodeScreen(navigateTo = navigateTo)
                }
            }
        }
    }
}

/**
 * This indicator syncs up the tab indicator with the [HorizontalPager] position.
 * We may add this in the library at some point.
 */
@OptIn(ExperimentalPagerApi::class)
fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
): Modifier = composed {
    val targetIndicatorOffset: Dp
    val indicatorWidth: Dp

    val currentTab = tabPositions[pagerState.currentPage]
    val nextTab = tabPositions.getOrNull(pagerState.currentPage + 1)
    if (nextTab != null) {
        // If we have a next tab, lerp between the size and offset
        targetIndicatorOffset = lerp(currentTab.left, nextTab.left, pagerState.currentPageOffset)
        indicatorWidth = lerp(currentTab.width, nextTab.width, pagerState.currentPageOffset)
    } else {
        // Otherwise we just use the current tab/page
        targetIndicatorOffset = currentTab.left
        indicatorWidth = currentTab.width
    }

    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = targetIndicatorOffset)
        .width(indicatorWidth)
}