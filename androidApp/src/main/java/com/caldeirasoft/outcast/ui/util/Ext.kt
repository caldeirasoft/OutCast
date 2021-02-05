package com.caldeirasoft.outcast.ui.util

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.AmbientContentAlpha
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

typealias ComposableFn = @Composable () -> Unit
typealias ComposableRowFn = @Composable RowScope.() -> Unit
typealias ComposableColumnFn = @Composable ColumnScope.() -> Unit
typealias TypedComposableFn<T> = @Composable (T) -> Unit

val Int.px: Float @Composable get() = with(AmbientDensity.current) { this@px.dp.toPx()}

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