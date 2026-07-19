package com.caloshape.app.data.activity.test

import com.caloshape.app.data.activity.sync.DataOriginPrefs


object OriginPicker {

    
    fun choosePreferredOrigin(
        byOrigin: Map<String, Long>,
        preferred: List<String>
    ): String? {
        if (byOrigin.isEmpty()) return null

        fun stepsOf(pkg: String) = byOrigin[pkg]


        for (pkg in preferred) {
            if (pkg == DataOriginPrefs.ON_DEVICE_ANDROID) continue
            val v = stepsOf(pkg)
            if (v != null && v > 0L) return pkg
        }


        if (preferred.contains(DataOriginPrefs.ON_DEVICE_ANDROID)) {
            return byOrigin.maxByOrNull { it.value }?.key
        }


        for (pkg in preferred) {
            if (pkg == DataOriginPrefs.ON_DEVICE_ANDROID) continue
            if (byOrigin.containsKey(pkg)) return pkg
        }

        return null
    }
}
