package com.caldeirasoft.outcast.ui.util

import androidx.compose.ui.graphics.Color
import com.caldeirasoft.outcast.ui.theme.hsvToColor

class ColorHash(
        val string: String,
        val lightness: List<Double> = listOf(0.5),
        val saturation: List<Double> =  listOf(0.2, 0.35, 0.5, 0.65, 0.8),
        val minHue: Int = 0,
        val maxHue: Int = 360
) {
    companion object {
        val SEED = 131L
        val SEED2 = 37L
        val MAX_SAFE_LONG = 65745979961613L
    }

    fun toHSL(): HSL {
        var hash = bkdrHash(string)
        val hue = ((hash % 359) / 1000f) * (maxHue - minHue) + minHue
        hash = (hash / 360)
        val sat = this.saturation[(hash % this.saturation.size).toInt()]
        hash = (hash / saturation.size)
        val light = this.lightness[(hash % this.lightness.size).toInt()]
        return HSL(hue.toDouble(), sat, light)
    }

    fun toRGB() = toHSL().toRGB()
    fun toHexString() = toRGB().toHex()
    fun toColor() = toRGB().toColor()

    private fun bkdrHash(string: String): Long {
        return (string + 'x').fold(0L) { acc, value ->
            when {
                acc > MAX_SAFE_LONG -> acc / SEED2
                else -> acc * SEED + value.code.toLong()
            }
        }
    }

    data class HSL(val hue: Double, val saturation: Double, val lightness: Double) {
        fun toRGB(): RGB {
            val h = hue / 360f

            val q = when {
                (lightness < 0.5) -> lightness * (1f + saturation)
                else -> lightness + saturation - lightness * saturation
            }

            val p = 2f * lightness - q

            val rgb = listOf(h + 1f / 3f, h, h - 1f / 3f).map { color ->
                val co = when {
                    color < 0 -> color + 1
                    color > 1 -> color - 1
                    else -> color
                }

                val c = when {
                    (co < 1f / 6f) -> p + (q - p) * 6.0 * co
                    (co < 0.5) -> q
                    (co < 2f / 3f) -> p + (q - p) * 6.0 * ( 2f / 3f - co)
                    else -> p
                }
                Math.max(0, Math.round(c * 255).toInt())
            }

            return RGB(rgb[0], rgb[1], rgb[2])
        }

        fun toColor(): Color =
            hsvToColor(hue.toFloat(), saturation.toFloat(), lightness.toFloat())
    }

    data class RGB(val red: Int, val green: Int, val blue: Int) {
        fun toHex(): String {
            return "#" + listOf(red, green, blue)
                .reversed()
                .map {
                    when {
                        it < 16 -> "0${Integer.toHexString(it)}"
                        else -> Integer.toHexString(it)
                    }
                }.reduce { s, acc -> acc + s }
        }

        fun toColor(): Color =
            Color(red, green, blue)
    }
}