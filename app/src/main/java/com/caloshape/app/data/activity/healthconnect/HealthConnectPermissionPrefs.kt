package com.caloshape.app.data.activity.healthconnect

import android.content.Context
import kotlin.math.max
import kotlin.math.min
import androidx.core.content.edit


object HealthConnectPermissionPrefs {

    private const val PREF = "hc_permission_prefs"
    private const val KEY_DENIED_COUNT = "hc_denied_count"

    fun getDeniedCount(ctx: Context): Int {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getInt(KEY_DENIED_COUNT, 0)
    }

    fun incrementDeniedCount(ctx: Context) {
        val sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val next = min(max(sp.getInt(KEY_DENIED_COUNT, 0) + 1, 0), 99)
        sp.edit { putInt(KEY_DENIED_COUNT, next) }
    }

    fun resetDeniedCount(ctx: Context) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit {
                putInt(KEY_DENIED_COUNT, 0)
            }
    }
}
