package com.caloshape.app.ui.home.ui.progress

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.caloshape.app.ui.common.design.CaloShapeColors
import com.caloshape.app.ui.home.components.HomeCardStyles
import java.time.LocalDate

internal object ProgressChartAxisDefaults {
    val GridColor = Color(0xFFBDBDBD)
    val IdleLabelColor = Color(0xFF8A8A8E)
    val TodayLabelColor = Color(0xFF4B5563)

    val IdleLabelWeight = FontWeight.Normal
    val TodayLabelWeight = FontWeight.Bold

    @Composable
    fun gridColor(): Color {
        val colors = CaloShapeColors.current()
        return if (colors.background == CaloShapeColors.Dark.background) {
            HomeCardStyles.Chart.grid()
        } else {
            GridColor
        }
    }

    @Composable
    fun idleLabelColor(): Color {
        val colors = CaloShapeColors.current()
        return if (colors.background == CaloShapeColors.Dark.background) {
            HomeCardStyles.Chart.idleLabel()
        } else {
            IdleLabelColor
        }
    }

    @Composable
    fun todayLabelColor(): Color {
        val colors = CaloShapeColors.current()
        return if (colors.background == CaloShapeColors.Dark.background) {
            HomeCardStyles.Chart.todayLabel()
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
