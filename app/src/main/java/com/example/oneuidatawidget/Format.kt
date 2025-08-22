package com.example.oneuidatawidget

import kotlin.math.roundToInt

object Format {
    fun bytesToGbString(bytes: Long): String {
        val gb = bytes / (1024.0 * 1024 * 1024)
        return String.format("%.2f GB", gb)
    }
    fun percentOfCap(bytes: Long, capGb: Double): Int {
        val total = capGb * 1024.0 * 1024 * 1024
        return ((bytes / total) * 1000).coerceAtMost(1000.0).roundToInt()
    }
}
