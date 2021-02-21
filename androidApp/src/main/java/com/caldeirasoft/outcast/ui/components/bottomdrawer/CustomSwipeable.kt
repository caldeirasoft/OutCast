package com.caldeirasoft.outcast.ui.components.bottomdrawer

import androidx.compose.animation.core.*
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.gestures.draggable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.nestedscroll.NestedScrollConnection
import androidx.compose.ui.gesture.nestedscroll.NestedScrollSource
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.sign

/**
 * State of the [swipeable] modifier.
 *
 * This contains necessary information about any ongoing swipe or animation and provides methods
 * to change the state either immediately or by starting an animation. To create and remember a
 * [CustomSwipeableState] with the default animation clock, use [rememberSwipeableState].
 *
 * @param initialValue The initial value of the state.
 * @param clock The animation clock that will be used to drive the animations.
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@Stable
@ExperimentalMaterialApi
open class CustomSwipeableState<T>(
    initialValue: T,
    clock: AnimationClockObservable,
    internal val animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    internal val confirmStateChange: (newValue: T) -> Boolean = { true }
) {
    /**
     * The current value of the state.
     *
     * If no swipe or animation is in progress, this corresponds to the anchor at which the
     * [swipeable] is currently settled. If a swipe or animation is in progress, this corresponds
     * the last anchor at which the [swipeable] was settled before the swipe or animation started.
     */
    var value: T by mutableStateOf(initialValue)
        private set

    /**
     * Whether the state is currently animating.
     */
    var isAnimationRunning: Boolean by mutableStateOf(false)
        private set

    // Use `Float.NaN` as a placeholder while the state is uninitialised.
    private val offsetState = mutableStateOf(Float.NaN)
    private val overflowState = mutableStateOf(Float.NaN)

    /**
     * The current position (in pixels) of the [swipeable].
     *
     * You should use this state to offset your content accordingly. The recommended way is to
     * use `Modifier.offsetPx`. This includes the resistance by default, if resistance is enabled.
     */
    val offset: State<Float> get() = offsetState

    /**
     * The amount by which the [swipeable] has been swiped past its bounds.
     */
    val overflow: State<Float> get() = overflowState

    internal var minBound = Float.NEGATIVE_INFINITY
    internal var maxBound = Float.POSITIVE_INFINITY

    private val anchorsState = mutableStateOf(emptyMap<Float, T>())

    // TODO(calintat): Remove this when b/151158070 is fixed.
    private val animationClockProxy: AnimationClockObservable = object : AnimationClockObservable {
        override fun subscribe(observer: AnimationClockObserver) {
            isAnimationRunning = true
            clock.subscribe(observer)
        }

        override fun unsubscribe(observer: AnimationClockObserver) {
            isAnimationRunning = false
            clock.unsubscribe(observer)
        }
    }

    internal var anchors: Map<Float, T>
        get() {
            return anchorsState.value
        }
        set(anchors) {
            if (anchorsState.value.isEmpty()) {
                // If this is the first time that we receive anchors, then we need to initialise
                // the state so we snap to the offset associated to the initial value.
                minBound = anchors.keys.minOrNull()!!
                maxBound = anchors.keys.maxOrNull()!!
                val initialOffset = anchors.getOffset(value)
                requireNotNull(initialOffset) {
                    "The initial value must have an associated anchor."
                }
                holder.snapTo(initialOffset)
            } else if (anchors != anchorsState.value) {
                // If we have received new anchors, then the offset of the current value might
                // have changed, so we need to animate to the new offset. If the current value
                // has been removed from the anchors then we animate to the closest anchor
                // instead. Note that this stops any ongoing animation.
                minBound = Float.NEGATIVE_INFINITY
                maxBound = Float.POSITIVE_INFINITY
                val targetOffset = anchors.getOffset(value)
                    ?: anchors.keys.minByOrNull { abs(it - offset.value) }!!
                holder.animateTo(
                    targetOffset, animationSpec,
                    onEnd = { endReason, _ ->
                        value = anchors.getValue(targetOffset)
                        minBound = anchors.keys.minOrNull()!!
                        maxBound = anchors.keys.maxOrNull()!!
                        // If the animation was interrupted for any reason, snap as a last resort.
                        if (endReason == AnimationEndReason.Interrupted) holder.snapTo(targetOffset)
                    }
                )
            }
            anchorsState.value = anchors
        }

    internal var thresholds: (Float, Float) -> Float by mutableStateOf({ _, _ -> 0f })

    internal var velocityThreshold by mutableStateOf(0f)

    internal var resistance: ResistanceConfig? by mutableStateOf(null)

    internal val holder: AnimatedFloat = NotificationBasedAnimatedFloat(0f, animationClockProxy) {
        val clamped = it.coerceIn(minBound, maxBound)
        val overflow = it - clamped
        val resistanceDelta = resistance?.computeResistance(overflow) ?: 0f
        offsetState.value = clamped + resistanceDelta
        overflowState.value = overflow
    }

    /**
     * The target value of the state.
     *
     * If a swipe is in progress, this is the value that the [swipeable] would animate to if the
     * swipe finished. If an animation is running, this is the target value of that animation.
     * Finally, if no swipe or animation is in progress, this is the same as the [value].
     */
    @ExperimentalMaterialApi
    val targetValue: T
        get() {
            val target = if (isAnimationRunning) {
                holder.targetValue
            } else {
                // TODO(calintat): Track current velocity (b/149549482) and use that here.
                computeTarget(
                    offset = offset.value,
                    lastValue = anchors.getOffset(value) ?: offset.value,
                    anchors = anchors.keys,
                    thresholds = thresholds,
                    velocity = 0f,
                    velocityThreshold = Float.POSITIVE_INFINITY
                )
            }
            return anchors[target] ?: value
        }

    /**
     * Information about the ongoing swipe or animation, if any. See [SwipeProgress] for details.
     *
     * If no swipe or animation is in progress, this returns `SwipeProgress(value, value, 1f)`.
     */
    @ExperimentalMaterialApi
    val progress: SwipeProgress<T>
        get() {
            val bounds = findBounds(offset.value, anchors.keys)
            val from: T
            val to: T
            val fraction: Float
            when (bounds.size) {
                0 -> {
                    from = value
                    to = value
                    fraction = 1f
                }
                1 -> {
                    from = anchors.getValue(bounds[0])
                    to = anchors.getValue(bounds[0])
                    fraction = 1f
                }
                else -> {
                    val (a, b) =
                        if (direction > 0f) {
                            bounds[0] to bounds[1]
                        } else {
                            bounds[1] to bounds[0]
                        }
                    from = anchors.getValue(a)
                    to = anchors.getValue(b)
                    fraction = (offset.value - a) / (b - a)
                }
            }
            return SwipeProgress(from, to, fraction)
        }

    /**
     * The direction in which the [swipeable] is moving, relative to the current [value].
     *
     * This will be either 1f if it is is moving from left to right or top to bottom, -1f if it is
     * moving from right to left or bottom to top, or 0f if no swipe or animation is in progress.
     */
    @ExperimentalMaterialApi
    val direction: Float
        get() = anchors.getOffset(value)?.let { sign(offset.value - it) } ?: 0f

    /**
     * Set the state to the target value immediately, without any animation.
     *
     * @param targetValue The new target value to set [value] to.
     */
    @ExperimentalMaterialApi
    fun snapTo(targetValue: T) {
        val targetOffset = anchors.getOffset(targetValue)
        requireNotNull(targetOffset) {
            "The target value must have an associated anchor."
        }
        value = targetValue
        holder.snapTo(targetOffset)
    }

    /**
     * Set the state to the target value by starting an animation.
     *
     * @param targetValue The new value to animate to.
     * @param anim The animation that will be used to animate to the new value.
     * @param onEnd Optional callback that will be invoked when the animation ended for any reason.
     */
    @ExperimentalMaterialApi
    fun animateTo(
        targetValue: T,
        anim: AnimationSpec<Float> = animationSpec,
        onEnd: ((AnimationEndReason, T) -> Unit)? = null
    ) {
        val targetOffset = anchors.getOffset(targetValue)
        requireNotNull(targetOffset) {
            "The target value must have an associated anchor."
        }
        holder.animateTo(targetOffset, anim) { endReason, endOffset ->
            val endValue = anchors[endOffset] ?: value
            value = endValue
            onEnd?.invoke(endReason, endValue)
        }
    }

    /**
     * Perform fling with settling to one of the anchors which is determined by the given
     * [velocity]. Fling with settling [swipeable] will always consume all the velocity provided
     * since it will settle at the anchor.
     *
     * In general cases, [swipeable] flings by itself when being swiped. This method is to be
     * used for nested scroll logic that wraps the [swipeable]. In nested scroll developer may
     * want to trigger settling fling when the child scroll container reaches the bound.
     *
     * @param velocity velocity to fling and settle with
     * @param onEnd callback to be invoked when fling is completed
     */
    fun performFling(velocity: Float, onEnd: (() -> Unit)) {
        val lastAnchor = anchors.getOffset(value)!!
        val targetValue = computeTarget(
            offset = offset.value,
            lastValue = lastAnchor,
            anchors = anchors.keys,
            thresholds = thresholds,
            velocity = velocity,
            velocityThreshold = velocityThreshold
        )
        val targetState = anchors[targetValue]
        if (targetState != null && confirmStateChange(targetState)) {
            animateTo(targetState, onEnd = { _, _ -> onEnd() })
        } else {
            // If the user vetoed the state change, rollback to the previous state.
            holder.animateTo(lastAnchor, animationSpec, onEnd = { _, _ -> onEnd() })
        }
    }

    /**
     * Force [swipeable] to consume drag delta provided from outside of the regular [swipeable]
     * gesture flow.
     *
     * Note: This method performs generic drag and it won't settle to any particular anchor, *
     * leaving swipeable in between anchors. When done dragging, [performFling] must be
     * called as well to ensure swipeable will settle at the anchor.
     *
     * In general cases, [swipeable] drags by itself when being swiped. This method is to be
     * used for nested scroll logic that wraps the [swipeable]. In nested scroll developer may
     * want to force drag when the child scroll container reaches the bound.
     *
     * @param delta delta in pixels to drag by
     *
     * @return the amount of [delta] consumed
     */
    fun performDrag(delta: Float): Float {
        val potentiallyConsumed = holder.value + delta
        val clamped = potentiallyConsumed.coerceIn(minBound, maxBound)
        val deltaToConsume = clamped - holder.value
        if (abs(deltaToConsume) > 0) {
            holder.snapTo(holder.value + deltaToConsume)
        }
        return deltaToConsume
    }

    companion object {
        /**
         * The default [Saver] implementation for [CustomSwipeableState].
         */
        fun <T : Any> Saver(
            clock: AnimationClockObservable,
            animationSpec: AnimationSpec<Float>,
            confirmStateChange: (T) -> Boolean
        ) = androidx.compose.runtime.saveable.Saver<CustomSwipeableState<T>, T>(
            save = { it.value },
            restore = { CustomSwipeableState(it, clock, animationSpec, confirmStateChange) }
        )
    }
}

/**
 * Enable swipe gestures between a set of predefined states.
 *
 * To use this, you must provide a map of anchors (in pixels) to states (of type [T]).
 * Note that this map cannot be empty and cannot have two anchors mapped to the same state.
 *
 * When a swipe is detected, the offset of the [SwipeableState] will be updated with the swipe
 * delta. You should use this offset to move your content accordingly (see `Modifier.offsetPx`).
 * When the swipe ends, the offset will be animated to one of the anchors and when that anchor is
 * reached, the value of the [SwipeableState] will also be updated to the state corresponding to
 * the new anchor. The target anchor is calculated based on the provided positional [thresholds].
 *
 * Swiping is constrained between the minimum and maximum anchors. If the user attempts to swipe
 * past these bounds, a resistance effect will be applied by default. The amount of resistance at
 * each edge is specified by the [resistance] config. To disable all resistance, set it to `null`.
 *
 * For an example of a [swipeable] with three states, see:
 *
 * @sample androidx.compose.material.samples.SwipeableSample
 *
 * @param T The type of the state.
 * @param state The state of the [swipeable].
 * @param anchors Pairs of anchors and states, used to map anchors to states and vice versa.
 * @param thresholds Specifies where the thresholds between the states are. The thresholds will be
 * used to determine which state to animate to when swiping stops. This is represented as a lambda
 * that takes two states and returns the threshold between them in the form of a [ThresholdConfig].
 * Note that the order of the states corresponds to the swipe direction.
 * @param orientation The orientation in which the [swipeable] can be swiped.
 * @param enabled Whether this [swipeable] is enabled and should react to the user's input.
 * @param reverseDirection Whether to reverse the direction of the swipe, so a top to bottom
 * swipe will behave like bottom to top, and a left to right swipe will behave like right to left.
 * @param interactionState Optional [InteractionState] that will passed on to [Modifier.draggable].
 * @param resistance Controls how much resistance will be applied when swiping past the bounds.
 * @param velocityThreshold The threshold (in dp per second) that the end velocity has to exceed
 * in order to animate to the next state, even if the positional [thresholds] have not been reached.
 */
@ExperimentalMaterialApi
fun <T> Modifier.swipeable(
    state: CustomSwipeableState<T>,
    anchors: Map<Float, T>,
    orientation: Orientation,
    enabled: Boolean = true,
    reverseDirection: Boolean = false,
    interactionState: InteractionState? = null,
    thresholds: (from: T, to: T) -> ThresholdConfig = { _, _ -> FixedThreshold(56.dp) },
    resistance: ResistanceConfig? = SwipeableDefaults.resistanceConfig(anchors.keys),
    velocityThreshold: Dp = SwipeableDefaults.VelocityThreshold
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "swipeable"
        properties["state"] = state
        properties["anchors"] = anchors
        properties["orientation"] = orientation
        properties["enabled"] = enabled
        properties["reverseDirection"] = reverseDirection
        properties["interactionState"] = interactionState
        properties["thresholds"] = thresholds
        properties["resistance"] = resistance
        properties["velocityThreshold"] = velocityThreshold
    }
) {
    require(anchors.isNotEmpty()) {
        "You must have at least one anchor."
    }
    require(anchors.values.distinct().count() == anchors.size) {
        "You cannot have two anchors mapped to the same state."
    }
    /*
    onCommit {
        state.resistance = resistance
    }
     */

    Modifier.draggable(
        orientation = orientation,
        enabled = enabled,
        reverseDirection = reverseDirection,
        interactionState = interactionState,
        startDragImmediately = state.isAnimationRunning,
        onDragStopped = { velocity ->
            state.performFling(velocity) {}
        }
    ) { delta ->
        state.holder.snapTo(state.holder.value + delta)
    }
}

private class NotificationBasedAnimatedFloat(
    initialValue: Float,
    clock: AnimationClockObservable,
    val onNewValue: (Float) -> Unit
) : AnimatedFloat(clock) {
    override var value = initialValue
        set(value) {
            field = value
            onNewValue(value)
        }
}

/**
 *  Given an offset x and a set of anchors, return a list of anchors:
 *   1. [ ] if the set of anchors is empty,
 *   2. [ x ] if x is equal to one of the anchors,
 *   3. [ min ] if min is the minimum anchor and x < min,
 *   4. [ max ] if max is the maximum anchor and x > max, or
 *   5. [ a , b ] if a and b are anchors such that a < x < b and b - a is minimal.
 */
private fun findBounds(
    offset: Float,
    anchors: Set<Float>
): List<Float> {
    // Find the anchors the target lies between with a little bit of rounding error.
    val a = anchors.filter { it <= offset + 0.001 }.maxOrNull()
    val b = anchors.filter { it >= offset - 0.001 }.minOrNull()

    return when {
        a == null ->
            // case 1 or 3
            listOfNotNull(b)
        b == null ->
            // case 4
            listOf(a)
        a == b ->
            // case 2
            listOf(offset)
        else ->
            // case 5
            listOf(a, b)
    }
}

private fun computeTarget(
    offset: Float,
    lastValue: Float,
    anchors: Set<Float>,
    thresholds: (Float, Float) -> Float,
    velocity: Float,
    velocityThreshold: Float
): Float {
    val bounds = findBounds(offset, anchors)
    return when (bounds.size) {
        0 -> lastValue
        1 -> bounds[0]
        else -> {
            val lower = bounds[0]
            val upper = bounds[1]
            if (lastValue <= offset) {
                // Swiping from lower to upper (positive).
                if (velocity >= velocityThreshold) {
                    return upper
                } else {
                    val threshold = thresholds(lower, upper)
                    if (offset < threshold) lower else upper
                }
            } else {
                // Swiping from upper to lower (negative).
                if (velocity <= -velocityThreshold) {
                    return lower
                } else {
                    val threshold = thresholds(upper, lower)
                    if (offset > threshold) upper else lower
                }
            }
        }
    }
}

private fun <T> Map<Float, T>.getOffset(state: T): Float? {
    return entries.firstOrNull { it.value == state }?.key
}

// temp default nested scroll connection for swipeables which desire as an opt in
// revisit in b/174756744 as all types will have their own specific connection probably
@ExperimentalMaterialApi
internal val <T> CustomSwipeableState<T>.PreUpPostDownNestedScrollConnection: NestedScrollConnection
    get() = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.toFloat()
            return if (delta < 0 && source == NestedScrollSource.Drag) {
                performDrag(delta).toOffset()
            } else {
                Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            return if (source == NestedScrollSource.Drag) {
                performDrag(available.toFloat()).toOffset()
            } else {
                Offset.Zero
            }
        }

        override fun onPreFling(available: Velocity): Velocity {
            val toFling = Offset(available.x, available.y).toFloat()
            return if (toFling < 0 && offset.value > minBound) {
                performFling(velocity = toFling) {}
                // since we go to the anchor with tween settling, consume all for the best UX
                available
            } else {
                Velocity.Zero
            }
        }

        override fun onPostFling(
            consumed: Velocity,
            available: Velocity,
            onFinished: (Velocity) -> Unit
        ) {
            performFling(velocity = Offset(available.x, available.y).toFloat()) {
                // since we go to the anchor with tween settling, consume all for the best UX
                onFinished.invoke(available)
            }
        }

        private fun Float.toOffset(): Offset = Offset(0f, this)

        private fun Offset.toFloat(): Float = this.y
    }
