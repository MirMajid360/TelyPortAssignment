package com.majid.androidassignment.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {

    fun formatDateToString(date: Long, format: String): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        dateFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val formattedDate = Date(date)
        return dateFormat.format(formattedDate)
    }

}