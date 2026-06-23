package com.caloshape.app.data.fasting.notifications

import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

data class TriggerTimes(
    val nextStart: Instant,
    val nextEnd: Instant,
    val endSoon: Instant
)

object NextTriggerCalculator {

    /**
     * ?¨æœ¬?°æ??€è¨ˆç?ä¸‹ä?æ¬?start/endï¼ˆDST-safeï¼šç”¨ ZonedDateTimeï¼?
     *
     * è¦å?ï¼?
     * - ?¥ä?å¤©ç? startTime ?„æ?????nextStart=ä»Šå¤© startTime
     * - ?¦å? ??nextStart=?å¤© startTime
     * - nextEnd = nextStart + eatingHours
     * - endSoon = nextEnd - 1 hour
     */
    fun compute(
        startTime: LocalTime,
        eatingHours: Int,
        zoneId: ZoneId,
        now: Instant = Instant.now()
    ): TriggerTimes {
        val nowZ = now.atZone(zoneId)

        var startZ: ZonedDateTime = nowZ.toLocalDate()
            .atTime(startTime)
            .atZone(zoneId)

        // ??isBefore ?å?ä¸€å¤©ï??¥å?å¥½ç???startTimeï¼Œä??‰æ??Œç¾?¨ã€ï?
        if (startZ.isBefore(nowZ)) {
            startZ = startZ.plusDays(1)
        }

        val endZ = startZ.plusHours(eatingHours.toLong())
        val endSoonZ = endZ.minusHours(1)

        return TriggerTimes(
            nextStart = startZ.toInstant(),
            nextEnd = endZ.toInstant(),
            endSoon = endSoonZ.toInstant()
        )
    }
}
