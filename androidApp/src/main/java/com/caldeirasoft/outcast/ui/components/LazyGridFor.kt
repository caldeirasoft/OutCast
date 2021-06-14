package com.caldeirasoft.outcast.ui.components

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
fun <T> ColumnScope.Grid(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    mainAxisSpacing: Dp = 0.dp,
    crossAxisSpacing: Dp = 0.dp,
    columns: Int = 2,
    items: List<T>,
    child: @Composable (item: T) -> Unit
) {
    val mainAxisPadding = PaddingValues(start = mainAxisSpacing, end = mainAxisSpacing)
    val crossAxisPadding =
        PaddingValues(start = crossAxisSpacing / columns, end = crossAxisSpacing / columns)
    val rows = items.chunked(columns)
    rows.forEachIndexed { index, rowList ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(mainAxisPadding)
        ) {
            rowList.forEachIndexed { rowIndex, it ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(contentPadding)
                ) {
                    child(it)
                }

                if (rowIndex < columns - 1) {
                    VerticalDivider()
                }
            }
            val emptyRows = (columns - rowList.size)
            repeat(emptyRows) {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .padding(contentPadding)
                )
                if (it + rowList.size < columns - 1) {
                    VerticalDivider()
                }
            }
        }
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
    gridItems(
        count = lazyPagingItems.itemCount,
        columns = columns,
        contentPadding = contentPadding,
        verticalInnerPadding = verticalInnerPadding,
        horizontalInnerPadding = horizontalInnerPadding
    ) { index ->
        val item = lazyPagingItems.getAsState(index = index)
        itemContent(item.value)
    }
}

fun <T : Any> LazyListScope.gridItemsIndexed(
    lazyPagingItems: LazyPagingItems<T>,
    columns: Int = 3,
    contentPadding: PaddingValues = PaddingValues(),
    verticalInnerPadding: Dp = 0.dp,
    horizontalInnerPadding: Dp = 0.dp,
    itemContent: @Composable LazyItemScope.(index: Int, value: T?) -> Unit
) {
    gridItems(
        count = lazyPagingItems.itemCount,
        columns = columns,
        contentPadding = contentPadding,
        verticalInnerPadding = verticalInnerPadding,
        horizontalInnerPadding = horizontalInnerPadding
    ) { index ->
        val item = lazyPagingItems.getAsState(index = index)
        itemContent(index, item.value)
    }
}

fun <T : Any> LazyListScope.gridItems(
    items: List<T>,
    columns: Int = 3,
    contentPadding: PaddingValues = PaddingValues(),
    verticalInnerPadding: Dp = 0.dp,
    horizontalInnerPadding: Dp = 0.dp,
    itemContent: @Composable LazyItemScope.(value: T) -> Unit
) {
    val rows = when {
        items.size % columns == 0 -> items.size / columns
        else -> (items.size / columns) + 1
    }

    for (row in 0..rows) {
        if (row == 0) spacerItem(contentPadding.calculateTopPadding())

        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = contentPadding.calculateEndPadding(LayoutDirection.Ltr)
                    )
            ) {
                for (column in 0 until columns) {
                    Box(modifier = Modifier.weight(1f)) {
                        val index = (row * columns) + column
                        if (index < items.size) {
                            itemContent(items[index])
                        }
                    }
                    if (column < columns - 1) {
                        Spacer(modifier = Modifier.width(horizontalInnerPadding))
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

fun LazyListScope.gridItems(
    count: Int,
    columns: Int = 3,
    contentPadding: PaddingValues = PaddingValues(),
    verticalInnerPadding: Dp = 0.dp,
    horizontalInnerPadding: Dp = 0.dp,
    itemContent: @Composable LazyItemScope.(index: Int) -> Unit
) {
    val rows = when {
        count % columns == 0 -> count / columns
        else -> (count / columns) + 1
    }

    for (row in 0..rows) {
        if (row == 0) spacerItem(contentPadding.calculateTopPadding())

        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = contentPadding.calculateEndPadding(LayoutDirection.Ltr)
                    )
            ) {
                for (column in 0 until columns) {
                    Box(modifier = Modifier.weight(1f)) {
                        val index = (row * columns) + column
                        if (index < count) {
                            itemContent(index)
                        }
                    }
                    if (column < columns - 1) {
                        Spacer(modifier = Modifier.width(horizontalInnerPadding))
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
        Spacer(
            Modifier
                .height(height)
                .fillParentMaxWidth())
    }
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
        modifier
            .then(indentMod)
            .fillMaxHeight()
            .width(thickness)
            .background(color = color)
    )
}

private const val DividerAlpha = 0.12f
