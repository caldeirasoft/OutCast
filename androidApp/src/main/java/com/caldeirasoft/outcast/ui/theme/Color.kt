package com.caldeirasoft.outcast.ui.theme

import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.launch

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

fun Color.Companion.getColor(colorString: String): Color {
    return Color(android.graphics.Color.parseColor("#$colorString"))
}

fun Color.Companion.blendARGB(
    color1: Color, color2: Color,
    @FloatRange(from = 0.0, to = 1.0) ratio: Float,
): Color {
    val inverseRatio = 1 - ratio
    val a = color1.alpha * inverseRatio + color2.alpha * ratio
    val r =
        color1.red * inverseRatio + color2.red * ratio
    val g = color1.green * inverseRatio + color2.green * ratio
    val b = color1.blue * inverseRatio + color2.blue * ratio
    return Color(red = r, green = g, blue = b, alpha = a)
}

@Composable
fun FetchDominantColorFromPoster(
    posterUrl: String,
    colorState: MutableState<Color>,
    defaultColor: Color = Color.randomColor()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(posterUrl) {
        scope.launch {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(posterUrl)
                .size(128, 128)
                .allowHardware(false)
                .build()

            val bitmap = (loader.execute(request) as? SuccessResult)?.drawable?.toBitmap()
                ?: return@launch
            val dominantColor =
                Palette.from(bitmap).generate().getVibrantColor(defaultColor.toArgb())
            colorState.value = Color(dominantColor)
        }
    }
}