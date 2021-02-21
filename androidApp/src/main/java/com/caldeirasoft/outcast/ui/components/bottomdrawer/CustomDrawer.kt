/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caldeirasoft.outcast.ui.components.bottomdrawer;

import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.nestedscroll.nestedScroll
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.gesture.tapGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.android.material.math.MathUtils.lerp
import kotlin.math.roundToInt

/**
 * Possible values of [DrawerState].
 */
enum class DrawerValue {
    /**
     * The state of the drawer when it is closed.
     */
    Closed,

    /**
     * The state of the drawer when it is open.
     */
    Open
}

/**
 * Possible values of [BottomDrawerState].
 */
enum class CustomBottomDrawerValue {
    /**
     * The state of the bottom drawer when it is closed.
     */
    Closed,

    /**
     * The state of the bottom drawer when it is open (i.e. at 50% height).
     */
    Open,

    /**
     * The state of the bottom drawer when it is expanded (i.e. at 100% height).
     */
    Expanded
}

/**
 * State of the [BottomDrawerLayout] composable.
 *
 * @param initialValue The initial value of the state.
 * @param clock The animation clock that will be used to drive the animations.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@Suppress("NotCloseable")
@OptIn(ExperimentalMaterialApi::class)
class CustomBottomDrawerState(
    initialValue: CustomBottomDrawerValue,
    clock: AnimationClockObservable,
    confirmStateChange: (CustomBottomDrawerValue) -> Boolean = { true }
) : CustomSwipeableState<CustomBottomDrawerValue>(
    initialValue = initialValue,
    clock = clock,
    animationSpec = AnimationSpec,
    confirmStateChange = confirmStateChange
) {
    /**
     * Whether the drawer is open.
     */
    val isOpen: Boolean
        get() = value == CustomBottomDrawerValue.Open

    /**
     * Whether the drawer is closed.
     */
    val isClosed: Boolean
        get() = value == CustomBottomDrawerValue.Closed

    /**
     * Whether the drawer is expanded.
     */
    val isExpanded: Boolean
        get() = value == CustomBottomDrawerValue.Expanded

    /**
     * Open the drawer with an animation.
     *
     * @param onOpened Optional callback invoked when the drawer has finished opening.
     */
    fun open(onOpened: (() -> Unit)? = null) {
        animateTo(
            CustomBottomDrawerValue.Open,
            onEnd = { endReason, endValue ->
                if (endReason != AnimationEndReason.Interrupted &&
                    endValue == CustomBottomDrawerValue.Open
                ) {
                    onOpened?.invoke()
                }
            }
        )
    }

    /**
     * Close the drawer with an animation.
     *
     * @param onClosed Optional callback invoked when the drawer has finished closing.
     */
    fun close(onClosed: (() -> Unit)? = null) {
        animateTo(
            CustomBottomDrawerValue.Closed,
            onEnd = { endReason, endValue ->
                if (endReason != AnimationEndReason.Interrupted &&
                    endValue == CustomBottomDrawerValue.Closed
                ) {
                    onClosed?.invoke()
                }
            }
        )
    }

    /**
     * Expand the drawer with an animation.
     *
     * @param onExpanded Optional callback invoked when the drawer has finished expanding.
     */
    fun expand(onExpanded: (() -> Unit)? = null) {
        animateTo(
            CustomBottomDrawerValue.Expanded,
            onEnd = { endReason, endValue ->
                if (endReason != AnimationEndReason.Interrupted &&
                    endValue == CustomBottomDrawerValue.Expanded
                ) {
                    onExpanded?.invoke()
                }
            }
        )
    }

    internal val nestedScrollConnection = this.PreUpPostDownNestedScrollConnection

    companion object {
        /**
         * The default [Saver] implementation for [BottomDrawerState].
         */
        fun Saver(
            clock: AnimationClockObservable,
            confirmStateChange: (CustomBottomDrawerValue) -> Boolean
        ) = Saver<CustomBottomDrawerState, CustomBottomDrawerValue>(
            save = { it.value },
            restore = { CustomBottomDrawerState(it, clock, confirmStateChange) }
        )
    }
}

/**
 * Create and [remember] a [BottomDrawerState] with the default animation clock.
 *
 * @param initialValue The initial value of the state.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@Composable
fun rememberBottomDrawerState(
    initialValue: CustomBottomDrawerValue,
    confirmStateChange: (CustomBottomDrawerValue) -> Boolean = { true }
): CustomBottomDrawerState {
    val clock = AmbientAnimationClock.current.asDisposableClock()
    return rememberSaveable(
        clock,
        saver = CustomBottomDrawerState.Saver(clock, confirmStateChange)
    ) {
        CustomBottomDrawerState(initialValue, clock, confirmStateChange)
    }
}

/**
 * Navigation drawers provide access to destinations in your app.
 *
 * Bottom navigation drawers are modal drawers that are anchored
 * to the bottom of the screen instead of the left or right edge.
 * They are only used with bottom app bars.
 *
 * These drawers open upon tapping the navigation menu icon in the bottom app bar.
 * They are only for use on mobile.
 *
 * See [ModalDrawerLayout] for a layout that introduces a classic from-the-side drawer.
 *
 * @sample androidx.compose.material.samples.BottomDrawerSample
 *
 * @param drawerState state of the drawer
 * @param modifier optional modifier for the drawer
 * @param gesturesEnabled whether or not drawer can be interacted by gestures
 * @param drawerShape shape of the drawer sheet
 * @param drawerElevation drawer sheet elevation. This controls the size of the shadow below the
 * drawer sheet
 * @param drawerContent composable that represents content inside the drawer
 * @param drawerBackgroundColor background color to be used for the drawer sheet
 * @param drawerContentColor color of the content to use inside the drawer sheet. Defaults to
 * either the matching `onFoo` color for [drawerBackgroundColor], or, if it is not a color from
 * the theme, this will keep the same value set above this Surface.
 * @param scrimColor color of the scrim that obscures content when the drawer is open
 * @param bodyContent content of the rest of the UI
 *
 * @throws IllegalStateException when parent has [Float.POSITIVE_INFINITY] height
 */
@Composable
@OptIn(ExperimentalMaterialApi::class)
fun CustomBottomDrawerLayout(
    drawerContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    drawerState: CustomBottomDrawerState = rememberBottomDrawerState(CustomBottomDrawerValue.Closed),
    gesturesEnabled: Boolean = true,
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawerElevation: Dp = DrawerDefaults.Elevation,
    drawerBackgroundColor: Color = MaterialTheme.colors.surface,
    drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
    scrimColor: Color = DrawerDefaults.scrimColor,
    bodyContent: @Composable () -> Unit
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        // TODO : think about Infinite max bounds case
        if (!constraints.hasBoundedHeight) {
            throw IllegalStateException("Drawer shouldn't have infinite height")
        }

        val minValue = 0f
        val maxValue = constraints.maxHeight.toFloat()

        // TODO: add proper landscape support
        val isLandscape = constraints.maxWidth > constraints.maxHeight
        val openValue = if (isLandscape) minValue else lerp(
            minValue,
            maxValue,
            BottomDrawerOpenFraction
        )
        val anchors =
            if (isLandscape) {
                mapOf(
                    maxValue to CustomBottomDrawerValue.Closed,
                    minValue to CustomBottomDrawerValue.Open
                )
            } else {
                mapOf(
                    maxValue to CustomBottomDrawerValue.Closed,
                    openValue to CustomBottomDrawerValue.Open,
                    minValue to CustomBottomDrawerValue.Expanded
                )
            }
        Box(
            Modifier
                .then(
                    if (drawerState.isClosed.not())
                        Modifier.nestedScroll(drawerState.nestedScrollConnection)
                    else Modifier
                )
                .swipeable(
                    state = drawerState,
                    anchors = anchors,
                    orientation = Orientation.Vertical,
                    enabled = false,
                    resistance = null
                )
        ) {
            Box {
                bodyContent()
            }
            Scrim(
                open = drawerState.isOpen,
                onClose = { drawerState.close() },
                fraction = {
                    // as we scroll "from height to 0" , need to reverse fraction
                    1 - calculateFraction(openValue, maxValue, drawerState.offset.value)
                },
                color = scrimColor
            )
            Surface(
                modifier = with(AmbientDensity.current) {
                    Modifier.preferredSizeIn(
                        minWidth = this@BoxWithConstraints.constraints.minWidth.toDp(),
                        minHeight = this@BoxWithConstraints.constraints.minHeight.toDp(),
                        maxWidth = this@BoxWithConstraints.constraints.maxWidth.toDp(),
                        maxHeight = this@BoxWithConstraints.constraints.maxHeight.toDp()
                    )
                }
                    .semantics {
                        if (drawerState.isOpen) {
                            dismiss(action = { drawerState.close(); true })
                        }
                    }.offset { IntOffset(0, drawerState.offset.value.roundToInt()) },
                shape = drawerShape,
                color = drawerBackgroundColor,
                contentColor = drawerContentColor,
                elevation = drawerElevation
            ) {
                Column(Modifier.fillMaxSize(), content = drawerContent)
            }
        }
    }
}

/**
 * Object to hold default values for [ModalDrawerLayout] and [BottomDrawerLayout]
 */
object DrawerDefaults {

    /**
     * Default Elevation for drawer sheet as specified in material specs
     */
    val Elevation = 16.dp

    val scrimColor: Color
        @Composable
        get() = MaterialTheme.colors.onSurface.copy(alpha = ScrimOpacity)

    /**
     * Default alpha for scrim color
     */
    const val ScrimOpacity = 0.32f
}

private fun calculateFraction(a: Float, b: Float, pos: Float) =
    ((pos - a) / (b - a)).coerceIn(0f, 1f)

@Composable
private fun Scrim(
    open: Boolean,
    onClose: () -> Unit,
    fraction: () -> Float,
    color: Color
) {
    val dismissDrawer = if (open) {
        Modifier.tapGestureFilter { onClose() }
    } else {
        Modifier
    }

    Canvas(
        Modifier
            .fillMaxSize()
            .then(dismissDrawer)
    ) {
        drawRect(color, alpha = fraction())
    }
}

private val VerticalDrawerPadding = 56.dp
private val DrawerVelocityThreshold = 400.dp

private const val DrawerStiffness = 1000f

private val AnimationSpec = SpringSpec<Float>(stiffness = DrawerStiffness)

internal const val BottomDrawerOpenFraction = 0.5f
