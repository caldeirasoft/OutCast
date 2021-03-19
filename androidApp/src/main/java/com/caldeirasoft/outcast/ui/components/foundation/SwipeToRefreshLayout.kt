import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val RefreshDistance = 80.dp
private val MinRefreshDistance = 32.dp

@Composable
private fun rememberSwipeToRefreshState(
    scope: CoroutineScope,
    initialValue: Boolean,
    initialOffset: Float,
    maxOffset: Float,
    minOffset: Float,
    onRefresh: () -> Unit,
): SwipeToRefreshState {
    // Avoid creating a new instance every invocation
    val saver = remember(
        scope,
        initialValue,
        initialOffset,
        maxOffset,
        minOffset,
        onRefresh,
    ) {
        Saver<SwipeToRefreshState, Boolean>(
            save = {
                it.value
            },
            restore = {
                SwipeToRefreshState(
                    it,
                    scope = scope,
                    initialOffset = initialOffset,
                    maxOffset = maxOffset,
                    minOffset = minOffset,
                    onRefresh = onRefresh,
                )
            }
        )
    }
    return rememberSaveable(
        saver = saver,
    ) {
        SwipeToRefreshState(
            scope = scope,
            initialValue = initialValue,
            initialOffset = initialOffset,
            maxOffset = maxOffset,
            minOffset = minOffset,
            onRefresh = onRefresh,
        )
    }
}

@Stable
private class SwipeToRefreshState(
    initialValue: Boolean,
    private val scope: CoroutineScope,
    private val initialOffset: Float,
    private val minOffset: Float,
    private val maxOffset: Float,
    private val onRefresh: () -> Unit,
) : NestedScrollConnection {

    var value: Boolean by mutableStateOf(initialValue)

    val offset: Float
        get() = _offset.value

    private var _offset = Animatable(
        if (initialValue) {
            minOffset
        } else {
            initialOffset
        },
    )

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.toFloat()
        return if (delta < 0 && source == NestedScrollSource.Drag) {
            drag(delta).toOffset()
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset {
        return if (source == NestedScrollSource.Drag) {
            drag(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val toFling = Offset(available.x, available.y).toFloat()
        return if (toFling < 0) {
            Velocity.Zero
        } else {
            fling()
            Velocity.Zero
        }
    }

    override suspend fun onPostFling(
        consumed: Velocity,
        available: Velocity,
    ): Velocity {
        fling()
        return available
    }

    suspend fun snapTo(value: Float) {
        _offset.snapTo(value)
    }

    suspend fun fling() {
        val offsetValue = _offset.value
        when {
            offsetValue >= 0 -> {
                if (!value) {
                    value = true
                    onRefresh.invoke()
                }
                _offset.animateTo(minOffset)
            }
            else -> {
                _offset.animateTo(initialOffset)
            }
        }
    }

    fun drag(delta: Float): Float {
        return if (!value && (delta > 0 || offset > initialOffset)) {
            scope.launch {
                snapTo((offset + delta).coerceAtMost(maxOffset))
            }
            delta
        } else {
            0f
        }
    }

    suspend fun animateTo(refreshingState: Boolean) {
        value = refreshingState
        when {
            refreshingState -> {
                _offset.animateTo(minOffset)
            }
            else -> {
                _offset.animateTo(initialOffset)
            }
        }
    }

    private fun Float.toOffset(): Offset = Offset(0f, this)

    private fun Offset.toFloat(): Float = this.y
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToRefreshLayout(
    modifier: Modifier = Modifier,
    refreshingState: Boolean,
    onRefresh: () -> Unit,
    refreshIndicator: @Composable () -> Unit = {
        Surface(elevation = 10.dp, shape = CircleShape) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(36.dp)
                    .padding(4.dp)
            )
        }
    },
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val refreshDistance = with(LocalDensity.current) { RefreshDistance.toPx() }
    val minRefreshDistance = with(LocalDensity.current) { MinRefreshDistance.toPx() }
    val state = rememberSwipeToRefreshState(
        scope = scope,
        initialValue = refreshingState,
        initialOffset = -refreshDistance,
        maxOffset = refreshDistance,
        minOffset = minRefreshDistance,
        onRefresh = onRefresh,
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(state)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        if (state.drag(dragAmount) != 0f) {
                            change.consumePositionChange()
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            state.fling()
                        }
                    }
                )
            }
    ) {
        content()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .offset { IntOffset(0, state.offset.roundToInt()) },
            contentAlignment = Alignment.TopCenter,
        ) {
            if (state.offset != -refreshDistance) {
                refreshIndicator()
            }
        }

        LaunchedEffect(refreshingState) {
            scope.launch {
                state.animateTo(refreshingState)
            }
        }
    }
}