package com.caldeirasoft.outcast.ui.util

import kotlin.time.DurationUnit
import kotlin.time.toDuration

object DurationFormatter {
    fun Int.formatDuration(): String {
        val duration = this.toDuration(DurationUnit.SECONDS)
        return duration.toComponents { hours, minutes, seconds, nanoseconds ->
            with(StringBuilder()) {
                val hasHours = hours != 0
                val hasSeconds = seconds != 0
                val hasMinutes = minutes != 0 || (hasSeconds && hasHours)
                if (hasHours) {
                    append(hours)
                    append(" h ")
                }
                if (hasMinutes) {
                    append(minutes.toString().padStart(2, '0'))
                    append(" min ")
                }
                if ((hasSeconds && !hasHours && minutes < 2) || (!hasHours && !hasMinutes)) {
                    append(seconds.toString().padStart(2, '0'))
                    append(" s")
                }
                trim()
                toString()
            }
        }
    }
}
