package com.caldeirasoft.outcast.ui.util

import android.content.Context
import android.widget.Toast

fun Context?.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this ?: return, message, length).show()
}
