package com.example.a4200gp.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private const val DATE_PATTERN = "EEE, MMM d yyyy 'at' h:mm a"

    fun epochToReadable(epochMs: Long): String {
        val format = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        return format.format(Date(epochMs))
    }

    fun readableToEpoch(dateString: String): Long {
        val format = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        return format.parse(dateString)?.time ?: 0L
    }
}
