package com.caldeirasoft.outcast.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caldeirasoft.outcast.domain.models.episode
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.util.ScreenFn
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState


@Composable
fun StoreCollectionDataContent(
    storeCollection: StoreCollectionData,
    openStoreDataDetail: (StoreData) -> Unit,
) {
    // header
    StoreHeadingSection(title = storeCollection.label)

    // room content
    LazyRow(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = storeCollection.items) { item ->
            when (item) {
                is StoreData -> {
                    Card(
                        backgroundColor = colors[0],
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .width(200.dp)
                            .clickable(onClick = { openStoreDataDetail(item) })
                    )
                    {
                        CoilImage(
                            data = item.getArtworkUrl(),
                            contentDescription = item.label,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(18 / 9f)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ChoiceChipTab(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
) {
    val backgroundColor: Color = when {
        selected -> MaterialTheme.colors.primary.copy(alpha = 0.3f)
        else -> Color.Transparent
    }
    val contentColor: Color = when {
        selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    }
    Tab(selected, onClick) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        )
        {
            OutlinedButton(
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = animateColorAsState(backgroundColor).value,
                    contentColor = animateColorAsState(contentColor).value,
                    disabledContentColor = MaterialTheme.colors.onSurface
                        .copy(alpha = ContentAlpha.disabled)
                ),
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 0.dp),
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
            ) {
                Text(
                    text = text,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        letterSpacing = 0.25.sp
                    )
                )
            }
        }
    }
}

val LazyListState.nestedScrollConnection: NestedScrollConnection
    get() {
        return object : NestedScrollConnection {
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {

                val firstVisibleItemInfo = layoutInfo.visibleItemsInfo.firstOrNull()
                if (firstVisibleItemInfo != null) {
                    val firstItemIndex = firstVisibleItemInfo.index
                    val firstItemOffset = Math.abs(firstVisibleItemInfo.offset)
                    val firstItemSize = firstVisibleItemInfo.size

                    if (firstItemOffset <= firstItemSize / 2) {
                        animateScrollToItem(firstItemIndex)
                    } else {
                        animateScrollToItem(firstItemIndex.plus(1))
                    }
                }

                return super.onPostFling(consumed, available)
            }
        }
    }