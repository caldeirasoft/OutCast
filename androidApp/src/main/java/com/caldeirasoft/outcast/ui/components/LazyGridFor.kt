package com.caldeirasoft.outcast.ui.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasureBlock
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import androidx.paging.compose.itemsIndexed
import kotlin.math.ceil

@Composable
fun <T> LazyGridFor(
    items: List<T> = listOf(),
    rows: Int = 3,
    hPadding: Int = 8,
    itemContent: @Composable LazyItemScope.(T, Int) -> Unit
) {
    val chunkedList = items.chunked(rows)
    LazyColumnForIndexed(items = chunkedList, modifier = Modifier.padding(horizontal = hPadding.dp)) { index, it ->
        if (index == 0) {
            Spacer(modifier = Modifier.preferredHeight(8.dp))
        }

        Row {
            it.forEachIndexed { rowIndex, item ->
                Box(modifier = Modifier.weight(1F).align(Alignment.Top).padding(8.dp),
                    contentAlignment = Alignment.Center) {
                    itemContent(item, index * rows + rowIndex)
                }
            }
            repeat(rows - it.size) {
                Box(modifier = Modifier.weight(1F).padding(8.dp)) {}
            }
        }
    }
}

fun <T : Any> LazyListScope.gridItems(
    lazyPagingItems: LazyPagingItems<T>,
    columns: Int = 3,
    contentPadding: PaddingValues = PaddingValues(),
    hPadding: Dp = 0.dp,
    vPadding: Dp = 0.dp,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    Log.d("itemCount", lazyPagingItems.itemCount.toString())
    val rows = when {
        lazyPagingItems.itemCount % columns == 0 -> lazyPagingItems.itemCount / columns
        else -> (lazyPagingItems.itemCount / columns) + 1
    }

    for (row in 0 until rows) {
        if (row == 0) spacerItem(contentPadding.top)

        item {
            Row(
                Modifier.fillMaxWidth()
                    .padding(start = contentPadding.start, end = contentPadding.end)
            ) {
                for (column in 0 until columns) {
                    Box(modifier = Modifier.weight(1f)) {
                        val index = (row * columns) + column
                        if (index < lazyPagingItems.itemCount) {
                            itemContent(lazyPagingItems[index])
                        }
                    }
                    if (column < columns - 1) {
                        Spacer(modifier = Modifier.preferredWidth(hPadding))
                    }
                }
            }
        }

        if (row < rows - 1) {
            spacerItem(vPadding)
        } else {
            spacerItem(contentPadding.bottom)
        }
    }
}

fun LazyListScope.spacerItem(height: Dp) {
    item {
        Spacer(Modifier.preferredHeight(height).fillParentMaxWidth())
    }
}

private fun shortestColumn(colHeights: IntArray): Int {
    var minHeight = Int.MAX_VALUE
    var column = 0
    colHeights.forEachIndexed { index, height ->
        if (height < minHeight) {
            minHeight = height
            column = index
        }
    }
    return column
}