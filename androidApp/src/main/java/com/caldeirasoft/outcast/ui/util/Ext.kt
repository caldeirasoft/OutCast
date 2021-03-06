package com.caldeirasoft.outcast.ui.util

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

typealias ComposableFn = @Composable () -> Unit
typealias ComposableRowFn = @Composable RowScope.() -> Unit
typealias ComposableColumnFn = @Composable ColumnScope.() -> Unit
typealias TypedComposableFn<T> = @Composable (T) -> Unit

val Int.px: Float @Composable get() = with(LocalDensity.current) { this@px.dp.toPx()}

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
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
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
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha) {
            ProvideTextStyle(textStyle, content)
        }
    }
}