package com.calai.bitecal.ui.home.ui.progress

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.calai.bitecal.ui.common.design.BiteCalColors
import java.time.LocalDate

internal object ProgressChartAxisDefaults {
    val GridColor = Color(0xFFBDBDBD)
    val IdleLabelColor = Color(0xFF8A8A8E)
    val TodayLabelColor = Color(0xFF4B5563)

    val IdleLabelWeight = FontWeight.Normal
    val TodayLabelWeight = FontWeight.Bold

    @Composable
    fun gridColor(): Color {
        val colors = BiteCalColors.current()
        return if (colors.background == BiteCalColors.Dark.background) {
            Color(0xFF34343A)
        } else {
            GridColor
        }
    }

    @Composable
    fun idleLabelColor(): Color {
        val colors = BiteCalColors.current()
        return if (colors.background == BiteCalColors.Dark.background) {
            colors.textMuted
        } else {
            IdleLabelColor
        }
    }

    @Composable
    fun todayLabelColor(): Color {
        val colors = BiteCalColors.current()
        return if (colors.background == BiteCalColors.Dark.background) {
            colors.textPrimary
        } else {
            TodayLabelColor
        }
    }

    fun isToday(dateIso: String, dayLabel: String): Boolean {
        val today = LocalDate.now()

        if (dateIso == today.toString()) {
            return true
        }

        // 用於空資料 placeholder：date 可能是 ""，但 dayLabel 仍是 Mon/Tue...
        return dateIso.isBlank() &&
                dayLabel.take(3).equals(
                    today.dayOfWeek.name.take(3),
                    ignoreCase = true
                )
    }
}
