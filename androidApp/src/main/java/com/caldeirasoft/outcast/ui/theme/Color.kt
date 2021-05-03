package com.caldeirasoft.outcast.ui.theme

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import kotlin.math.max
import kotlin.math.min

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

fun Color.Companion.getColor(colorString: String?): Color? {
    return colorString?.let { Color(android.graphics.Color.parseColor("#$colorString")) }
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

fun Color.toHsv(): FloatArray {
    val result = floatArrayOf(0f, 0f, 0f)
    android.graphics.Color.colorToHSV(toArgb(), result)
    return result
}

fun hsvToColor(hue: Float, saturation: Float, value: Float): Color {
    val f = floatArrayOf(hue, saturation, value)
    return Color(android.graphics.Color.HSVToColor(f))
}

fun Color.toxyY(): FloatArray {
    val X = red * 0.4124f + green * 0.3576f + blue * 0.1805f
    val Y = red * 0.2126f + green * 0.7152f + blue * 0.0722f
    val Z = red * 0.0193f + green * 0.1192f + blue * 0.9505f
    val L = (X + Y + Z)
    val x = X / L
    val y = Y / L
    val result = floatArrayOf(x, y, Y)
    return result
}

fun xyYtoColor(x: Float, y: Float, Y: Float): Color {
    // Convert from xyY to XYZ
    val x1 = x * Y / y
    val y1 = Y
    val z1 = (1 - x - y) * (Y / y)

    // Convert from XYZ to RGB
    var r = x1 * 3.2406f + y1 * -1.5372f + z1 * -0.4986f
    var g = x1 * -0.9689f + y1 * 1.8758f + z1 * 0.0415f
    var b = x1 * 0.0557f + y1 * -0.2040f + z1 * 1.0570f

    // assume sRGB
    if (r > 0.0031308) {
        r = (1.055f * Math.pow(r.toDouble(), 1.0 / 2.4) - 0.055f).toFloat()
    } else {
        r = r * 12.92f
    }
    if (g > 0.0031308) {
        g = (1.055f * Math.pow(g.toDouble(), 1.0 / 2.4) - 0.055f).toFloat()
    } else {
        g = g * 12.92f
    }
    if (b > 0.0031308) {
        b = (1.055f * Math.pow(b.toDouble(), 1.0 / 2.4) - 0.055f).toFloat()
    } else {
        b = b * 12.92f
    }

    return Color(
        red = r.coerceIn(0f, 1f),
        green = g.coerceIn(0f, 1f),
        blue = b.coerceIn(0f, 1f))
}

fun Color.constrastAgainst(background: Color): Float {
    val fg = if (alpha < 1f) compositeOver(background) else this

    val fgLuminance = fg.luminance() + 0.05f
    val bgLuminance = background.luminance() + 0.05f

    return max(fgLuminance, bgLuminance) / min(fgLuminance, bgLuminance)
}