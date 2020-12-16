package com.caldeirasoft.outcast.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.LaunchedTask
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.ContextAmbient
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult

val purple200 = Color(0xFFBB86FC)
val purple500 = Color(0xFF6200EE)
val purple700 = Color(0xFF3700B3)
val teal200 = Color(0xFF03DAC5)

val colors = listOf(
    Color(0xFFffd7d7.toInt()),
    Color(0xFFffe9d6.toInt()),
    Color(0xFFfffbd0.toInt()),
    Color(0xFFe3ffd9.toInt()),
    Color(0xFFd0fff8.toInt())
)

private val colorRange = 0..256
fun Color.Companion.randomColor() = Color(colorRange.random(), colorRange.random(), colorRange.random())

@Composable
fun FetchDominantColorFromPoster(
    posterUrl: String,
    colorState: MutableState<Color>,
    defaultColor: Color = Color.randomColor()
) {
    val context = AmbientContext.current
    LaunchedEffect(posterUrl) {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(posterUrl)
            .size(128, 128)
            .allowHardware(false)
            .build()

        val bitmap = (loader.execute(request) as? SuccessResult)?.drawable?.toBitmap() ?: return@LaunchedEffect
        val dominantColor = Palette.from(bitmap).generate().getVibrantColor(defaultColor.toArgb())
        colorState.value = Color(dominantColor)
    }
}