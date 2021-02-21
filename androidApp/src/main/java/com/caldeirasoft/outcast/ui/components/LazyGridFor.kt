package com.caldeirasoft.outcast.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems

@Composable
fun <T> LazyGridFor(
    items: List<T> = listOf(),
    columns: Int = 3,
    hPadding: Int = 8,
    itemContent: @Composable BoxScope.(T, Int) -> Unit
) {
    val chunkedList = items.chunked(columns)
    Column(modifier = Modifier
        .padding(horizontal = hPadding.dp)
        .padding(top = 8.dp)) {
        chunkedList.forEachIndexed { index, list ->
            Row {
                /*list.forEachIndexed { rowIndex, item ->
                    Box(modifier = Modifier.weight(1F).align(Alignment.Top).padding(8.dp),
                        contentAlignment = Alignment.Center)
                    {
                        //itemContent(item, index * columns + rowIndex)
                    }
                }*/
                repeat(columns - list.size) {
                    Box(modifier = Modifier
                        .weight(1F)
                        .padding(8.dp)) {}
                }
            }
        }
    }
}

@Composable
fun <T> ColumnScope.Grid(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    mainAxisSpacing: Dp = 0.dp,
    crossAxisSpacing: Dp = 0.dp,
    columns: Int = 2,
    items: List<T>,
    rowHeight: Dp,
    child: @Composable (item: T, innerPadding: PaddingValues) -> Unit
) {
    val mainAxisPadding = PaddingValues(start = mainAxisSpacing, end = mainAxisSpacing)
    val crossAxisPadding =
        PaddingValues(start = crossAxisSpacing / columns, end = crossAxisSpacing / columns)
    val rows = items.chunked(columns)
    rows.forEachIndexed { index, rowList ->
        Row(modifier = Modifier
            .fillMaxWidth()
            .preferredHeight(rowHeight)
            .padding(mainAxisPadding)) {
            rowList.forEachIndexed { rowIndex, it ->
                Box(modifier = Modifier
                    .weight(1f)
                    .padding(contentPadding)) {
                    child(it, crossAxisPadding)
                }

                if (rowIndex < columns - 1) {
                    VerticalDivider()
                }
            }
            val emptyRows = (columns - rowList.size)
            repeat(emptyRows) {
                Spacer(modifier = Modifier
                    .weight(1f)
                    .padding(contentPadding))
                if (it + rowList.size < columns - 1) {
                    VerticalDivider()
                }
            }
        }

        if (index < rows.size - 1)
            Divider()
    }
}

fun <T : Any> LazyListScope.gridItems(
    lazyPagingItems: LazyPagingItems<T>,
    columns: Int = 3,
    contentPadding: PaddingValues = PaddingValues(),
    verticalInnerPadding: Dp = 0.dp,
    horizontalInnerPadding: Dp = 0.dp,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    Log.d("itemCount", lazyPagingItems.itemCount.toString())
    val rows = when {
        lazyPagingItems.itemCount % columns == 0 -> lazyPagingItems.itemCount / columns
        else -> (lazyPagingItems.itemCount / columns) + 1
    }

    for (row in 0..rows) {
        if (row == 0) spacerItem(contentPadding.calculateTopPadding())

        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = contentPadding.calculateStartPadding(LayoutDirection.Ltr), end = contentPadding.calculateEndPadding(LayoutDirection.Ltr))
            ) {
                for (column in 0 until columns) {
                    Box(modifier = Modifier.weight(1f)) {
                        val index = (row * columns) + column
                        if (index < lazyPagingItems.itemCount) {
                            itemContent(lazyPagingItems[index])
                        }
                    }
                    if (column < columns - 1) {
                        Spacer(modifier = Modifier.preferredWidth(horizontalInnerPadding))
                    }
                }
            }
        }

        if (row < rows - 1)
            spacerItem(verticalInnerPadding)
        else
            spacerItem(contentPadding.calculateBottomPadding())
    }
}

fun LazyListScope.spacerItem(height: Dp) {
    item {
        Spacer(Modifier
            .preferredHeight(height)
            .fillParentMaxWidth())
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

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = DividerAlpha),
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp
) {
    val indentMod = if (startIndent.value != 0f) {
        Modifier.padding(start = startIndent)
    } else {
        Modifier
    }
    Box(
        modifier.then(indentMod)
            .fillMaxHeight()
            .preferredWidth(thickness)
            .background(color = color)
    )
}

private const val DividerAlpha = 0.12f
