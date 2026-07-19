package com.caloshape.app.data.activity.sync

import com.caloshape.app.data.activity.model.DailyActivityStatus
import java.time.LocalDate
import java.time.ZoneId

interface DailyReader {
    suspend fun getStatus(): DailyActivityStatus
    suspend fun hasAnyRecord(localDate: LocalDate, zoneId: ZoneId, originPackage: String): Boolean
    suspend fun readSteps(localDate: LocalDate, zoneId: ZoneId, originPackage: String): Long?
    suspend fun resolveOriginName(packageName: String): String?

    
    suspend fun readStepsByOrigin(localDate: LocalDate, zoneId: ZoneId): Map<String, Long> = emptyMap()
}
