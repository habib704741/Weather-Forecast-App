package com.example.weatherforecastapp.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    fun convertTimestampToDay(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        return format.format(date)
    }
}