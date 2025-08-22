package com.example.oneuidatawidget

import android.content.Context
import android.net.TrafficStats
import java.util.*
import kotlin.math.max

object UsageRepository {

    private fun sp(ctx: Context) = ctx.getSharedPreferences("usage_baseline", Context.MODE_PRIVATE)

    data class Usage(val wifiBytes: Long, val mobileBytes: Long)

    fun readCurrent(): Usage {
        val mobile = (TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes()).coerceAtLeast(0)
        val total = (TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes()).coerceAtLeast(0)
        val wifi = max(0, total - mobile)
        return Usage(wifiBytes = wifi, mobileBytes = mobile)
    }

    fun ensureBaseline(ctx: Context, resetDay: Int) {
        val key = "baseline_${cycleStartKey(resetDay)}"
        if (!sp(ctx).contains(key + "_wifi")) {
            val u = readCurrent()
            sp(ctx).edit().putLong("${key}_wifi", u.wifiBytes).putLong("${key}_mob", u.mobileBytes).apply()
        }
    }

    fun refreshBaselineIfNeeded(ctx: Context, resetDay: Int) {
        val key = "baseline_${cycleStartKey(resetDay)}"
        val prefs = sp(ctx)
        if (!prefs.contains("${key}_wifi")) {
            val u = readCurrent()
            prefs.edit().putLong("${key}_wifi", u.wifiBytes).putLong("${key}_mob", u.mobileBytes).apply()
        }
    }

    fun usedThisCycle(ctx: Context, resetDay: Int): Usage {
        val key = "baseline_${cycleStartKey(resetDay)}"
        val baseWifi = sp(ctx).getLong("${key}_wifi", 0L)
        val baseMob  = sp(ctx).getLong("${key}_mob", 0L)
        val now = readCurrent()
        return Usage(
            wifiBytes = max(0, now.wifiBytes - baseWifi),
            mobileBytes = max(0, now.mobileBytes - baseMob)
        )
    }

    private fun cycleStartKey(resetDay: Int): String {
        val cal = Calendar.getInstance()
        val todayDay = cal.get(Calendar.DAY_OF_MONTH)
        if (todayDay < resetDay) {
            cal.add(Calendar.MONTH, -1)
        }
        cal.set(Calendar.DAY_OF_MONTH, resetDay)
        return "${cal.get(Calendar.YEAR)}_${cal.get(Calendar.MONTH)+1}_${resetDay}"
    }

    fun daysLeft(resetDay: Int): Int {
        val cal = Calendar.getInstance()
        val today = cal.clone() as Calendar
        val end = cal.clone() as Calendar
        val day = cal.get(Calendar.DAY_OF_MONTH)
        if (day >= resetDay) end.add(Calendar.MONTH, 1)
        end.set(Calendar.DAY_OF_MONTH, resetDay)
        val diff = ((end.timeInMillis - today.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
        return if (diff == 0) 30 else diff
    }
}
