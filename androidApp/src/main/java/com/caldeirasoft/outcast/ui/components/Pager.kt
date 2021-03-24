package com.caldeirasoft.outcast.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * This is a modified version of:
 * https://gist.github.com/adamp/07d468f4bcfe632670f305ce3734f511
 */
// I Added support for vertical direction as well.

class PagerState(currentPage: Int = 0, pages: Int = 0, val onPageChanged: (Int) -> Unit = {}) {
    var minPage = 0
    var maxPage: Int = pages

    private var _currentPage by mutableStateOf(currentPage.coerceIn(minPage, maxPage))
    var currentPage: Int
        get() = _currentPage
        set(value) {
            _currentPage = value.coerceIn(minPage, maxPage)
        }

    enum class SelectionState { Selected, Undecided }

    var selectionState by mutableStateOf(SelectionState.Selected)

    private suspend fun selectPage() {
        snapToOffset(0f)
        selectionState = SelectionState.Selected
    }

    private var _currentPageOffset = Animatable(0f).apply {
        updateBounds(-1f, 1f)
    }
    val currentPageOffset: Float
        get() = _currentPageOffset.value

    suspend fun snapToOffset(offset: Float) {
        val max = if (currentPage == minPage) 0f else 1f
        val min = if (currentPage == maxPage) 0f else -1f
        _currentPageOffset.snapTo(offset.coerceIn(min, max))
    }

    suspend fun fling(velocity: Float) {
        if (velocity < 0 && currentPage == maxPage) return
        if (velocity > 0 && currentPage == 0) return

        try {
            currentPage -= currentPageOffset.roundToInt()
            onPageChanged(currentPage)
            _currentPageOffset.animateDecay(velocity, exponentialDecay())
            _currentPageOffset.animateTo(currentPageOffset.roundToInt().toFloat())
            selectPage()
        }
        catch (c: CancellationException) {
            //Animation interrupted: Do nothing
        }
    }
}


class PagerScope(private val state: PagerState, val page: Int) {
    val currentPage get() = state.currentPage
}


@Immutable
private data class PageData(val page: Int) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@PageData
}

private val Measurable.page: Int
    get() = (parentData as? PageData)?.page ?: error("no PageData for measurable $this")

@Composable
fun Pager(
    state: PagerState,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    offscreenLimit: Int = 2,
    pageContent: @Composable PagerScope.() -> Unit,
) {
    var pageSize by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    Layout(
        content = {
            val minPage = (state.currentPage - offscreenLimit)
            val maxPage = (state.currentPage + offscreenLimit)

            for (page in minPage..maxPage) {
                val pageData = PageData(page)
                val scope = PagerScope(state, page)
                key(pageData) {
                    Box(contentAlignment = Alignment.Center,
                        modifier = pageData) {
                        scope.pageContent()
                    }
                }
            }
        },
        modifier = modifier.draggable(
            orientation = Orientation.Horizontal,
            state = rememberDraggableState(onDelta = { dy ->
                coroutineScope.launch {
                    with(state) {
                        val pos = pageSize * currentPageOffset
                        val max = if (currentPage == 0) 0 else pageSize * offscreenLimit
                        val min = if (currentPage == maxPage) 0 else -pageSize * offscreenLimit
                        val newPos = (pos + dy).coerceIn(min.toFloat(), max.toFloat())
                        snapToOffset(newPos / pageSize)
                    }
                }}),
            onDragStarted = { state.selectionState = PagerState.SelectionState.Undecided },
            onDragStopped = { velocity -> coroutineScope.launch { state.fling(velocity / pageSize) } }
        )
    )

    { measurable, constraints ->
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeableMap =
            measurable
                .map { it.measure(childConstraints) to it.page }
        val rowHeights = placeableMap.map { (placeable, _) -> placeable.height }
        val maxHeight = rowHeights
            .maxOrNull()
            ?.coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))
            ?: constraints.minHeight

        layout(constraints.maxWidth, maxHeight) {
            val currentPage = state.currentPage
            val offset = state.currentPageOffset

            placeableMap
                .forEach { (placeable, page) ->
                    val xCenterOffset = when (contentAlignment) {
                        Alignment.Start -> 0
                        Alignment.End -> constraints.maxWidth - placeable.width
                        Alignment.CenterHorizontally -> (constraints.maxWidth - placeable.width) / 2
                        else -> 0
                    }
                    val yCenterOffset = (constraints.maxHeight - placeable.height) / 2
                    if (currentPage == page) pageSize = placeable.width
                    val xItemOffset = ((page + offset - currentPage) * placeable.width).roundToInt()
                    placeable.place(x = xCenterOffset + xItemOffset, y = yCenterOffset)
                }
        }
    }
}