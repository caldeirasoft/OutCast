package com.caldeirasoft.outcast.ui.util

import android.content.Context
import com.caldeirasoft.outcast.R
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

fun Instant.isDateTheSame(dateTime: Instant): Boolean =
    this.toLocalDateTime(TimeZone.currentSystemDefault()).date !=
            dateTime.toLocalDateTime(TimeZone.currentSystemDefault()).date
