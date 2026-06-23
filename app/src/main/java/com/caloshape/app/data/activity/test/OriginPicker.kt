package com.caloshape.app.data.activity.test

import com.caloshape.app.data.activity.sync.DataOriginPrefs

/**
 * ?Šæ?ä¾†æ??è¼¯?½å‡ºä¾†ï??¹ä¾¿æ¸¬è©¦??debug??
 */
object OriginPicker {

    /**
     * è¦å?ï¼?
     * 1) ?ˆæ? preferred ä¸?steps > 0 ?„ï?å¿½ç•¥ "android" ?™å€?any-source ? ä?ï¼?
     * 2) ??preferred ?…å« "android"ï¼šå?è¨±ä»»ä½•ä?æºï???steps ?€å¤§ï??¯èƒ½??0ï¼?
     * 3) ?¦å?ï¼šåª??preferred ä¹‹ä¸­å­˜åœ¨?„ï??³ä½¿??0ï¼‰ï??½æ??‰æ? null
     */
    fun choosePreferredOrigin(
        byOrigin: Map<String, Long>,
        preferred: List<String>
    ): String? {
        if (byOrigin.isEmpty()) return null

        fun stepsOf(pkg: String) = byOrigin[pkg]

        // 1) ?ˆä??å¥½?¾ï?ä½†å???>0
        for (pkg in preferred) {
            if (pkg == DataOriginPrefs.ON_DEVICE_ANDROID) continue
            val v = stepsOf(pkg)
            if (v != null && v > 0L) return pkg
        }

        // 2) ?è¨±ä»»ä?ä¾†æ?ï¼šé¸ steps ?€å¤§ï??¯èƒ½ 0ï¼?
        if (preferred.contains(DataOriginPrefs.ON_DEVICE_ANDROID)) {
            return byOrigin.maxByOrNull { it.value }?.key
        }

        // 3) ä¸å?è¨?any-sourceï¼šæ??å¥½å­˜åœ¨?„ï??³ä½¿ 0ï¼?
        for (pkg in preferred) {
            if (pkg == DataOriginPrefs.ON_DEVICE_ANDROID) continue
            if (byOrigin.containsKey(pkg)) return pkg
        }

        return null
    }
}
