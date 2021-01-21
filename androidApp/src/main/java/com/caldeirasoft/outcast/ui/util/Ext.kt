package com.caldeirasoft.outcast.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.dp

val Int.px: Float @Composable get() = with(AmbientDensity.current) { this@px.dp.toPx()}