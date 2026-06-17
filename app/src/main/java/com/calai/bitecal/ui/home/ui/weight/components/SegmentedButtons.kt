package com.calai.bitecal.ui.home.ui.weight.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.calai.bitecal.data.profile.repo.UserProfileStore
import com.calai.bitecal.ui.home.components.HomeCardStyles

@Composable
fun SegmentedButtons(
    selected: UserProfileStore.WeightUnit,
    onSelect: (UserProfileStore.WeightUnit) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 108.dp,
    height: Dp = 40.dp,
    pillExtraWidth: Dp = 6.dp,
    labelPadding: Dp = 6.dp,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
) {
    val checked = (selected == UserProfileStore.WeightUnit.LBS) // 右側為 LBS
    val isDark = HomeCardStyles.isDark()

    WeightUnitSwitchLabeled(
        checked = checked,
        onCheckedChange = { isRight ->
            onSelect(if (isRight) UserProfileStore.WeightUnit.LBS else UserProfileStore.WeightUnit.KG)
        },
        modifier = modifier,
        width = width,
        height = height,
        padding = 3.dp,
        leftLabel = "kg",
        rightLabel = "lbs",
        trackBase = if (isDark) HomeCardStyles.Switch.trackOff() else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
        trackOn = if (isDark) HomeCardStyles.Switch.trackOn() else MaterialTheme.colorScheme.onSurface,
        textOn = if (isDark) Color(0xFF111114) else MaterialTheme.colorScheme.surface,
        textOff = if (isDark) HomeCardStyles.Text.secondary() else MaterialTheme.colorScheme.onSurface,
        textStyle = textStyle,
        pillExtraWidth = pillExtraWidth,
        labelPadding = labelPadding,
        trackBorderColor = if (isDark) HomeCardStyles.Surface.borderColor() else null,
        trackBorderWidth = if (isDark) 1.dp else 0.dp
    )
}
