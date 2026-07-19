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
