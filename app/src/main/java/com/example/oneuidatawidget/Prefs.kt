package com.example.oneuidatawidget

import android.content.Context
import kotlin.math.max

class Prefs(ctx: Context, private val id: Int) {
    private val sp = ctx.getSharedPreferences(if (id == 0) "oneui_prefs_global" else "oneui_prefs_$id", Context.MODE_PRIVATE)
    private val global = ctx.getSharedPreferences("oneui_prefs_global", Context.MODE_PRIVATE)

    private fun gInt(key: String, def: Int) = if (id == 0) sp.getInt(key, def) else sp.getInt(key, global.getInt(key, def))
    private fun gLong(key: String, def: Long) = if (id == 0) sp.getLong(key, def) else sp.getLong(key, global.getLong(key, def))
    private fun gBool(key: String, def: Boolean) = if (id == 0) sp.getBoolean(key, def) else sp.getBoolean(key, global.getBoolean(key, def))

    var alpha: Int
        get() = gInt("alpha", 220)
        set(v) { (if (id == 0) sp else global).edit().putInt("alpha", v).apply() }

    var resetDay: Int
        get() = gInt("resetDay", 1)
        set(v) { (if (id == 0) sp else global).edit().putInt("resetDay", v).apply() }

    var dataCapGb: Double
        get() {
            val raw = gLong("cap", java.lang.Double.doubleToRawLongBits(12.0))
            return java.lang.Double.longBitsToDouble(raw)
        }
        set(v) { (if (id == 0) sp else global).edit()
            .putLong("cap", java.lang.Double.doubleToRawLongBits(max(0.1, v))).apply() }

    var animations: Boolean
        get() = gBool("animations", true)
        set(v) { (if (id == 0) sp else global).edit().putBoolean("animations", v).apply() }
}
