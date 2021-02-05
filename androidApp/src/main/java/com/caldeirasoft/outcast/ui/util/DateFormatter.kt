package com.caldeirasoft.outcast.ui.util

import android.content.Context
import com.caldeirasoft.outcast.domain.util.Log_D
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.chrono.Chronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.*

object DateFormatter {
    private val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    private val today = now.date
    private val yesterday = today + DatePeriod(days = -1)
    private val lastWeek = today + DatePeriod(days = -7)
    private val lastYear = today + DatePeriod(years = -1)

    fun Instant.formatRelativeDisplay(context: Context): String {
        val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
        val localDate = localDateTime.date
        val diffWithLastWeek = localDate - lastWeek
        val diffWithLastYear = localDate - lastYear
        val df = getDateFormatterWithoutYear()
        return when {
            localDate == today -> {
                // SHORT: 2:05 AM
                localDateTime.toJavaLocalDateTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
            }
            localDate == yesterday -> "Yesterday"
            localDate > lastWeek -> localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            localDate > lastYear -> localDate.toJavaLocalDate().format(getDateFormatterWithoutYear())
            else -> localDate.toJavaLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
        }
    }

    private fun getDateFormatterWithoutYear(): DateTimeFormatter {
        val regex = "[^DdMm]*[Yy]+[^DdMm]*".toRegex()
        val regexEs = "[^Mm]*[Yy]+[^Mm]*".toRegex()
        val locale = Locale.getDefault()
        val builder = DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.LONG, null, Chronology.ofLocale(locale), locale)
        val pattern = if (builder.contains("de")) regexEs.replace(builder,"") else regex.replace(builder, "")
        return DateTimeFormatter.ofPattern(pattern)
    }
}
