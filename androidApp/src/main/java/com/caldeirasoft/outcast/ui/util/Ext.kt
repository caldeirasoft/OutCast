package com.caldeirasoft.outcast.ui.util

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.caldeirasoft.outcast.ui.components.bottomsheet.ModalBottomSheetContent
import com.caldeirasoft.outcast.ui.navigation.Screen
import kotlin.math.roundToInt

typealias ComposableFn = @Composable () -> Unit
typealias ComposableRowFn = @Composable RowScope.() -> Unit
typealias ComposableColumnFn = @Composable ColumnScope.() -> Unit
typealias TypedComposableFn<T> = @Composable (T) -> Unit
typealias ScreenFn = (Screen) -> Unit
typealias DialogFn = (ModalBottomSheetState, ModalBottomSheetContent, Screen) -> Unit

val Int.px: Float @Composable get() = with(AmbientDensity.current) { this@px.dp.toPx()}

@Composable
fun Dp.toPx(): Float = with(LocalDensity.current) { this@toPx.toPx() }

@Composable
fun Dp.toIntPx(): Int = this.toPx().roundToInt()

@Composable
fun Float.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }

@Composable
fun Int.toDp(): Dp = this.toFloat().toDp()

fun applyTextStyleCustom(
    textStyle: TextStyle,
    contentAlpha: Float,
    content: ComposableFn
): @Composable (() -> Unit) {
    return {
        Providers(AmbientContentAlpha provides contentAlpha) {
            ProvideTextStyle(textStyle, content)
        }
    }
}

fun applyTextStyleNullable(
    textStyle: TextStyle,
    contentAlpha: Float,
    content: ComposableFn?
): @Composable (() -> Unit)? {
    if (content == null) return null
    return {
        Providers(AmbientContentAlpha provides contentAlpha) {
            ProvideTextStyle(textStyle, content)
        }
    }
}