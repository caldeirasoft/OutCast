package com.caldeirasoft.outcast.ui.components

import androidx.compose.animation.AnimatedFloatModel
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.fling
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import kotlin.math.roundToInt

/**
 * This is a modified version of:
 * https://gist.github.com/adamp/07d468f4bcfe632670f305ce3734f511
 */
// I Added support for vertical direction as well.

class PagerState(
    clock: AnimationClockObservable,
    currentPage: Int = 0,
    minPage: Int = 0,
    maxPage: Int = 0
) {
    private var _minPage by mutableStateOf(minPage)
    var minPage: Int
        get() = _minPage
        set(value) {
            _minPage = value.coerceAtMost(_maxPage)
            _currentPage = _currentPage.coerceIn(_minPage, _maxPage)
        }

    private var _maxPage by mutableStateOf(maxPage, structuralEqualityPolicy())
    var maxPage: Int
        get() = _maxPage
        set(value) {
            _maxPage = value.coerceAtLeast(_minPage)
            _currentPage = _currentPage.coerceIn(_minPage, maxPage)
        }

    private var _currentPage by mutableStateOf(currentPage.coerceIn(minPage, maxPage))
    var currentPage: Int
        get() = _currentPage
        set(value) {
            _currentPage = value.coerceIn(minPage, maxPage)
        }

    enum class SelectionState { Selected, Undecided }

    var selectionState by mutableStateOf(SelectionState.Selected)

    inline fun <R> selectPage(block: PagerState.() -> R): R = try {
        selectionState = SelectionState.Undecided
        block()
    } finally {
        selectPage()
    }

    fun selectPage() {
        currentPage -= currentPageOffset.roundToInt()
        currentPageOffset = 0f
        selectionState = SelectionState.Selected
    }

    private var _currentPageOffset3 = Animatable(0f, Spring.DefaultDisplacementThreshold)

    private var _currentPageOffset = AnimatedFloatModel(0f, clock = clock).apply {
        setBounds(-1f, 1f)
    }

    var currentPageOffset: Float
        get() = _currentPageOffset.value
        set(value) {
            val max = if (currentPage == minPage) 0f else 1f
            val min = if (currentPage == maxPage) 0f else -1f
            _currentPageOffset.snapTo(value.coerceIn(min, max))
        }

    fun fling(velocity: Float) {
        if (velocity < 0 && currentPage == maxPage) return
        if (velocity > 0 && currentPage == minPage) return

        _currentPageOffset.fling(velocity) { _, _, _ ->
            selectPage()
        }
    }

    override fun toString(): String = "PagerState{minPage=$minPage, maxPage=$maxPage, " +
            "currentPage=$currentPage, currentPageOffset=$currentPageOffset}"
}

@Immutable
private data class PageData(val page: Int) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any? = this@PageData
}

private val Measurable.page: Int
    get() = (parentData as? PageData)?.page ?: error("no PageData for measurable $this")

@Composable
fun Pager(
    modifier: Modifier = Modifier,
    state: PagerState,
    orientation: Orientation = Orientation.Horizontal,
    contentAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    offscreenLimit: Int = 2,
    content: @Composable PagerScope.() -> Unit
) {
    var pageSize by remember { mutableStateOf(0) }
    val minPage = (state.currentPage - offscreenLimit).coerceAtLeast(state.minPage)
    val maxPage = (state.currentPage + offscreenLimit).coerceAtMost(state.maxPage)

    Layout(
        content = {
            for (page in minPage..maxPage) {
                val pageData = PageData(page)
                val scope = PagerScope(state, page)
                key(pageData) {
                    Column(modifier = pageData) {
                        scope.content()
                    }
                }
            }
        },
        modifier = modifier.draggable(
            orientation = orientation,
            onDragStarted = {
                state.selectionState = PagerState.SelectionState.Undecided
            },
            onDragStopped = { velocity ->
                // Velocity is in pixels per second, but we deal in percentage offsets, so we
                // need to scale the velocity to match
                state.fling(velocity / pageSize)
            }
        ) { dy ->
            with(state) {
                val pos = pageSize * currentPageOffset
                val max = if (currentPage == minPage) 0 else pageSize * offscreenLimit
                val min = if (currentPage == maxPage) 0 else -pageSize * offscreenLimit
                val newPos = (pos + dy).coerceIn(min.toFloat(), max.toFloat())
                currentPageOffset = newPos / pageSize
            }
        }
    ) { measurables, constraints ->
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeableMap =
            measurables
                .map {
                    it.measure(childConstraints) to it.page
                }
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
                    val xCenterOffset =
                        when (contentAlignment) {
                            Alignment.Start -> 0
                            Alignment.End -> (constraints.maxWidth - placeable.width)
                            Alignment.CenterHorizontally ->
                                (constraints.maxWidth - placeable.width) / 2
                            else ->
                                (constraints.maxWidth - placeable.width) / 2
                        }
                    val yCenterOffset = (maxHeight - placeable.height) / 2

                    if (currentPage == page) {
                        pageSize = if (orientation == Orientation.Horizontal) {
                            placeable.width
                        } else {
                            placeable.height
                        }
                    }
                    if (orientation == Orientation.Horizontal) {
                        placeable.place(
                            x = xCenterOffset + ((page - (currentPage - offset)) * placeable.width).roundToInt(),
                            y = yCenterOffset
                        )
                    } else {
                        placeable.place(
                            x = xCenterOffset,
                            y = yCenterOffset + ((page - (currentPage - offset)) * placeable.height).roundToInt()
                        )
                    }
                }
        }
    }
}

/**
 * Scope for [Pager] content.
 */
class PagerScope(
    private val state: PagerState,
    val page: Int
) {
    /**
     * Returns the current selected page
     */
    val currentPage: Int
        get() = state.currentPage

    /**
     * Returns the current selected page offset
     */
    val currentPageOffset: Float
        get() = state.currentPageOffset

    /**
     * Returns the current selection state
     */
    val selectionState: PagerState.SelectionState
        get() = state.selectionState

}