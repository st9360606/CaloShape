package com.calai.bitecal.ui.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.calai.bitecal.ui.common.design.BiteCalColors

/**
 * Home cards style source of truth.
 *
 * Scope:
 * - CalendarStrip
 * - Calories / macro / micronutrient / health-score cards
 * - Steps / workout / weight / fasting / water cards
 *
 * Keep this object as the single place to tune shared Home card visual tokens.
 */
object HomeCardStyles {
    @Composable
    fun isDark(): Boolean = BiteCalColors.current().background == BiteCalColors.Dark.background

    object Surface {
        val Bg = Color(0xFFFDFDFE)
        val BorderColor = Color(0xFFE0E2E6)
        val Border = BorderStroke(1.2.dp, BorderColor)
        val Corner = RoundedCornerShape(20.dp)

        private val DarkBg = Color(0xFF18151F)
        private val DarkRaised = Color(0xFF24212D)
        private val DarkRaisedAlt = Color(0xFF2A2633)
        private val DarkBorderColor = Color(0xFF34303D)

        @Composable
        fun card(): Color = if (HomeCardStyles.isDark()) DarkBg else Bg

        @Composable
        fun raised(): Color = if (HomeCardStyles.isDark()) DarkRaised else Color(0xFFF8FAFC)

        @Composable
        fun raisedAlt(): Color = if (HomeCardStyles.isDark()) DarkRaisedAlt else Color(0xFFF3F5F7)

        @Composable
        fun iconBackground(accent: Color): Color {
            return if (HomeCardStyles.isDark()) DarkRaised else accent.copy(alpha = 0.16f)
        }

        @Composable
        fun borderColor(): Color = if (HomeCardStyles.isDark()) DarkBorderColor else BorderColor

        @Composable
        fun border(): BorderStroke = BorderStroke(1.2.dp, borderColor())
    }

    object PanelHeights {
        val Metric = 132.dp
    }

    object Ring {
        val Size = 66.dp
        val Stroke = 5.dp
        val CenterDisk = 34.dp
        val Track = Color(0xFFEFF0F3)
        val CenterFill = Color(0xFFF5F7F9)

        private val DarkTrack = Color(0xFF302C3A)
        private val DarkCenterFill = Color(0xFF24212D)

        @Composable
        fun track(): Color = if (HomeCardStyles.isDark()) DarkTrack else Track

        @Composable
        fun centerFill(): Color = if (HomeCardStyles.isDark()) DarkCenterFill else CenterFill
    }

    object Palette {
        val Calories = Color(0xFF1F1A17)
        val Protein = Color(0xFFE56C6C)
        val Carbs = Color(0xFFD89A62)
        val Fats = Color(0xFF6C93D8)
        val Fiber = Color(0xFFA78BFA)
        val Sugar = Color(0xFFF08AAF)
        val Sodium = Color(0xFF73B6E6)
        val HealthScore = Color(0xFF5ECB7A)
        val Workout = Color(0xFFA37FE0)
        val Steps = Color(0xFF6BB8DA)
        val Weight = Color(0xFF5ECB7A)
        val Water = Color(0xFF73B6E6)
        val Fasting = Color(0xFF111114)

        @Composable
        fun calories(): Color = if (HomeCardStyles.isDark()) Color(0xFF8E87A3) else Calories

        @Composable
        fun protein(): Color = if (HomeCardStyles.isDark()) Color(0xFFD66D78) else Protein

        @Composable
        fun carbs(): Color = if (HomeCardStyles.isDark()) Color(0xFF9F93C7) else Carbs

        @Composable
        fun fats(): Color = if (HomeCardStyles.isDark()) Color(0xFF77A0E6) else Fats

        @Composable
        fun fiber(): Color = if (HomeCardStyles.isDark()) Color(0xFFA892F0) else Fiber

        @Composable
        fun sugar(): Color = if (HomeCardStyles.isDark()) Color(0xFFD986AA) else Sugar

        @Composable
        fun sodium(): Color = if (HomeCardStyles.isDark()) Color(0xFF74B4DF) else Sodium

        @Composable
        fun healthScore(): Color = if (HomeCardStyles.isDark()) Color(0xFF63C9A1) else HealthScore

        @Composable
        fun workout(): Color = if (HomeCardStyles.isDark()) Color(0xFF9F8BE8) else Workout

        @Composable
        fun steps(): Color = if (HomeCardStyles.isDark()) Color(0xFF79BDE0) else Steps

        @Composable
        fun weight(): Color = if (HomeCardStyles.isDark()) Color(0xFF71C99C) else Weight

        @Composable
        fun water(): Color = if (HomeCardStyles.isDark()) Color(0xFF73BFE8) else Water

        @Composable
        fun fasting(): Color = if (HomeCardStyles.isDark()) Color(0xFFC9C4D4) else Fasting
    }

    object Text {
        val Primary = Color(0xFF0F172A)
        val Secondary = Color(0xFF111114)
        val Label = Color(0xFF6B7280)

        @Composable
        fun primary(): Color = if (HomeCardStyles.isDark()) Color(0xFFF7F5FF) else Primary

        @Composable
        fun secondary(): Color = if (HomeCardStyles.isDark()) Color(0xFFC9C4D4) else Secondary

        @Composable
        fun label(): Color = if (HomeCardStyles.isDark()) Color(0xFF8F899C) else Label

        @Composable
        fun muted(): Color = if (HomeCardStyles.isDark()) Color(0xFF8F899C) else Color(0xFF71717A)
    }

    object TopBar {
        val Height = 26.dp
        val HorizontalPadding = 16.dp
        val Container = Color.Black
        val Content = Color.White
        val Shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

        @Composable
        fun container(): Color = if (HomeCardStyles.isDark()) Color(0xFF211D2A) else Container

        @Composable
        fun content(): Color = if (HomeCardStyles.isDark()) Color(0xFFF7F5FF) else Content
    }

    object Progress {
        @Composable
        fun track(): Color = if (HomeCardStyles.isDark()) Color(0xFF302C3A) else Color(0xFFF1F2F4)
    }

    object Action {
        @Composable
        fun primaryContainer(): Color = if (HomeCardStyles.isDark()) Color(0xFF73BFE8) else Color(0xFF111114)

        @Composable
        fun primaryContent(): Color = if (HomeCardStyles.isDark()) Color(0xFF11131A) else Color.White

        @Composable
        fun secondaryContainer(): Color = if (HomeCardStyles.isDark()) Color(0xFF24212D) else Color.White

        @Composable
        fun secondaryContent(): Color = if (HomeCardStyles.isDark()) Color(0xFFF7F5FF) else Color(0xFF111114)

        @Composable
        fun secondaryBorder(): Color = if (HomeCardStyles.isDark()) Color(0xFF4A4558) else Color(0xFF111114)

        @Composable
        fun flash(): Color = if (HomeCardStyles.isDark()) Color(0xFFC9C4D4) else Color.Black
    }

    object Switch {
        @Composable
        fun trackOn(): Color = if (HomeCardStyles.isDark()) Color(0xFF5CBF9A) else Color(0xFF5ECB7A)

        @Composable
        fun trackOff(): Color = if (HomeCardStyles.isDark()) Color(0xFF2A2633) else Color(0xFFE9ECEF)

        @Composable
        fun borderOff(): Color = if (HomeCardStyles.isDark()) Color(0xFF4A4558) else Color(0xFFD8DEE5)

        @Composable
        fun thumb(): Color = if (HomeCardStyles.isDark()) Color(0xFFC9C4D4) else Color.White
    }

    object Loading {
        @Composable
        fun skeletonBase(): Color = if (HomeCardStyles.isDark()) Color(0xFF2A2633) else Color(0xFFD7D7E0)

        @Composable
        fun skeletonHighlight(): Color = if (HomeCardStyles.isDark()) Color(0xFF413B4D) else Color(0xFFECECF3)

        @Composable
        fun thumbPlaceholder(): Color = if (HomeCardStyles.isDark()) Color(0xFF24212D) else Color(0xFFF2F3F6)

        @Composable
        fun ring(): Color = if (HomeCardStyles.isDark()) Color(0xFF8E87A3) else Color.White.copy(alpha = 0.95f)
    }

    object Calendar {
        val SelectedBackground = Color(0xFFFF8A33)
        val TodayBackground = Color.Gray
        val ActiveText = Color(0xFF111114)
        val DisabledText = Color(0xFF9CA3AF)
        val ActiveStroke = Color(0xFF111114)
        val DisabledStroke = Color(0xFFC1C7D0)

        val OnTargetStroke = Color(0xFF7DDF83)
        val SlightlyOverStroke = Color(0xFFB45309)
        val FarOverStroke = Color(0xFFD92D20)
        val NoMealStroke = Color(0xFF555A60)
        val TodayNoMealStroke = Color(0xFF2B3037)
    }
}

/**
 * Backward-compatible alias used by existing Home cards.
 */
object CardStyles {
    val Bg = HomeCardStyles.Surface.Bg
    val BorderColor = HomeCardStyles.Surface.BorderColor
    val Border = HomeCardStyles.Surface.Border
    val Corner = HomeCardStyles.Surface.Corner

    @Composable
    fun bg(): Color = BiteCalColors.current().surface

    @Composable
    fun border(): BorderStroke = BorderStroke(1.2.dp, BiteCalColors.current().border)
}
