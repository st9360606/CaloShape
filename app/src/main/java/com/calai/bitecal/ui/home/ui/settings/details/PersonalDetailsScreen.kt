package com.calai.bitecal.ui.home.ui.settings.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.calai.bitecal.R
import com.calai.bitecal.data.profile.api.UserProfileDto
import com.calai.bitecal.data.profile.repo.UserProfileStore
import com.calai.bitecal.ui.common.design.BiteCalCompactPillButton
import com.calai.bitecal.ui.common.design.BiteCalColors
import com.calai.bitecal.ui.common.design.BiteCalScreenFrame
import com.calai.bitecal.ui.common.design.BiteCalTopBar
import com.calai.bitecal.ui.common.haptic.biteCalClickable
import java.util.Locale
import kotlin.math.abs

private val PersonalCardShape = RoundedCornerShape(22.dp)

@Composable
private fun personalDetailsIconBackground(): Color {
    val colors = BiteCalColors.current()
    return if (colors == BiteCalColors.Dark) {
        Color(0xFF2A2633)
    } else {
        Color(0xFFF3F4F7)
    }
}

@Composable
private fun personalDetailsIconBorder(): Color {
    val colors = BiteCalColors.current()
    return if (colors == BiteCalColors.Dark) {
        Color(0xFF4A4558)
    } else {
        Color(0xFFE1E4EA)
    }
}

@Composable
private fun personalDetailsIconTint(): Color {
    val colors = BiteCalColors.current()
    return if (colors == BiteCalColors.Dark) {
        Color(0xFFF7F5FF)
    } else {
        Color(0xFF343840)
    }
}

@Composable
fun PersonalDetailsScreen(
    profile: UserProfileDto?,
    unit: UserProfileStore.WeightUnit,
    goalKgFromWeightVm: Double? = null,
    goalLbsFromWeightVm: Double? = null,
    currentKgFromTimeseries: Double? = null,
    currentLbsFromTimeseries: Double? = null,
    onBack: () -> Unit,
    onChangeGoal: () -> Unit,
    onEditCurrentWeight: () -> Unit = {},
    onEditHeight: () -> Unit = {},
    onEditAge: () -> Unit = {},
    onEditGender: () -> Unit = {},
    onEditDailyStepGoal: () -> Unit = {},
    onEditStartingWeight: () -> Unit = {},
    onEditDailyWaterGoal: () -> Unit = {},
    onEditDailyWorkoutGoal: () -> Unit = {}
) {
    val scroll = rememberScrollState()
    val colors = BiteCalColors.current()

    Scaffold(
        containerColor = colors.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            BiteCalTopBar(
                title = stringResource(R.string.settings_personal_details),
                onBack = onBack
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { inner ->
        Box(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .verticalScroll(scroll)
                    .padding(horizontal = BiteCalScreenFrame.contentHorizontalCompact)
                    .padding(
                        top = BiteCalScreenFrame.contentTopSmall,
                        bottom = BiteCalScreenFrame.detailBottom
                    )
                    .navigationBarsPadding()
            ) {
                val (goalMain, _) = formatWeightBothLines(
                    kg = goalKgFromWeightVm ?: profile?.goalWeightKg,
                    lbs = goalLbsFromWeightVm ?: profile?.goalWeightLbs,
                    unit = unit
                )
                val (curMain, _) = formatWeightBothLines(
                    kg = currentKgFromTimeseries ?: profile?.weightKg,
                    lbs = currentLbsFromTimeseries ?: profile?.weightLbs,
                    unit = unit
                )
                val (startMain, _) = formatWeightBothLines(
                    kg = profile?.weightKg,
                    lbs = profile?.weightLbs,
                    unit = unit
                )
                val ageText = profile?.age?.let {
                    stringResource(R.string.personal_details_age_value, it)
                } ?: emptyValue()
                val stepText = profile?.dailyStepGoal?.let {
                    stringResource(R.string.personal_details_steps_value, it)
                } ?: emptyValue()
                val waterText = profile?.waterMl?.let {
                    stringResource(R.string.personal_details_water_value, it)
                } ?: emptyValue()
                val workoutGoalText = profile?.dailyWorkoutGoalKcal?.let {
                    stringResource(R.string.personal_details_kcal_value, it)
                } ?: emptyValue()

                GoalWeightSummaryCard(
                    value = goalMain,
                    onChangeGoal = onChangeGoal
                )

                Spacer(Modifier.height(18.dp))

                PersonalSectionCard(
                    title = stringResource(R.string.personal_details_body_details_section)
                ) {
                    PersonalDetailsRow(
                        icon = Icons.Outlined.Tune,
                        title = stringResource(R.string.personal_details_current_weight),
                        valueMain = curMain,
                        onClick = onEditCurrentWeight
                    )
                    PersonalRowDivider()
                    PersonalDetailsRow(
                        icon = Icons.Outlined.Height,
                        title = stringResource(R.string.personal_details_height),
                        valueMain = formatHeight(profile),
                        onClick = onEditHeight
                    )
                    PersonalRowDivider()
                    PersonalDetailsRow(
                        icon = Icons.Outlined.CalendarMonth,
                        title = stringResource(R.string.personal_details_age),
                        valueMain = ageText,
                        onClick = onEditAge
                    )
                    PersonalRowDivider()
                    PersonalDetailsRow(
                        icon = Icons.Outlined.Person,
                        title = stringResource(R.string.personal_details_gender),
                        valueMain = formatGenderLabel(profile?.gender),
                        onClick = onEditGender
                    )
                    PersonalRowDivider()
                    PersonalDetailsRow(
                        icon = Icons.Outlined.MonitorWeight,
                        title = stringResource(R.string.personal_details_starting_weight),
                        valueMain = startMain,
                        onClick = onEditStartingWeight
                    )
                }

                Spacer(Modifier.height(16.dp))

                PersonalSectionCard(
                    title = stringResource(R.string.personal_details_daily_goals_section)
                ) {
                    PersonalDetailsRow(
                        icon = Icons.AutoMirrored.Outlined.DirectionsWalk,
                        title = stringResource(R.string.personal_details_daily_step_goal),
                        valueMain = stepText,
                        onClick = onEditDailyStepGoal
                    )
                    PersonalRowDivider()
                    PersonalDetailsRow(
                        icon = Icons.Outlined.WaterDrop,
                        title = stringResource(R.string.personal_details_daily_water_goal),
                        valueMain = waterText,
                        onClick = onEditDailyWaterGoal
                    )
                    PersonalRowDivider()
                    PersonalDetailsRow(
                        icon = Icons.Outlined.FitnessCenter,
                        title = stringResource(R.string.personal_details_daily_workout_goal),
                        valueMain = workoutGoalText,
                        onClick = onEditDailyWorkoutGoal
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun GoalWeightSummaryCard(
    value: String,
    onChangeGoal: () -> Unit
) {
    val colors = BiteCalColors.current()
    val iconBackground = personalDetailsIconBackground()
    val iconBorder = personalDetailsIconBorder()
    val iconTint = personalDetailsIconTint()

    PersonalDetailsSurface {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(iconBackground)
                    .border(1.dp, iconBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Flag,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(23.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.personal_details_goal_weight),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.sp
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = value,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        fontSize = 25.sp,
                        lineHeight = 30.sp,
                        letterSpacing = 0.sp
                    )
                )
            }

            BiteCalCompactPillButton(
                text = stringResource(R.string.personal_details_change_goal),
                onClick = onChangeGoal,
                modifier = Modifier.widthIn(min = 104.dp, max = 168.dp),
                height = 36.dp,
                textStyle = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.sp
                )
            )
        }
    }
}

@Composable
private fun PersonalSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    val colors = BiteCalColors.current()

    PersonalDetailsSurface {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                    letterSpacing = 0.sp
                ),
                modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 4.dp)
            )
            content()
        }
    }
}

@Composable
private fun PersonalDetailsSurface(content: @Composable () -> Unit) {
    val colors = BiteCalColors.current()

    Card(
        shape = PersonalCardShape,
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, colors.border),
        modifier = Modifier.fillMaxWidth()
    ) {
        content()
    }
}

@Composable
private fun PersonalDetailsRow(
    icon: ImageVector,
    title: String,
    valueMain: String,
    valueSub: String? = null,
    onClick: () -> Unit
) {
    val colors = BiteCalColors.current()
    val resolvedIconBg = personalDetailsIconBackground()
    val resolvedIconBorder = personalDetailsIconBorder()
    val resolvedIconTint = personalDetailsIconTint()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 66.dp)
            .biteCalClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(resolvedIconBg)
                .border(1.dp, resolvedIconBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = resolvedIconTint,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(Modifier.size(12.dp))

        Text(
            text = title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                letterSpacing = 0.sp
            ),
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            modifier = Modifier
                .padding(start = 10.dp)
                .widthIn(max = 156.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = valueMain,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 19.sp,
                        letterSpacing = 0.sp
                    ),
                    color = colors.textPrimary
                )
                if (valueSub != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = valueSub,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.textMuted,
                            letterSpacing = 0.sp
                        )
                    )
                }
            }

            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                tint = colors.textMuted.copy(alpha = 0.74f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

internal fun formatHeight(p: UserProfileDto?): String {
    val ft = p?.heightFeet
    val inch = p?.heightInches
    return if (ft != null && inch != null) {
        "$ft ft $inch in"
    } else {
        val cm = p?.heightCm
        if (cm == null) "--" else "${formatSmartNumber(cm)} cm"
    }
}

@Composable
private fun formatGenderLabel(raw: String?): String {
    val s = raw?.trim()?.lowercase(Locale.US).orEmpty()
    return when (s) {
        "male", "m" -> stringResource(R.string.gender_male)
        "female", "f" -> stringResource(R.string.gender_female)
        "other" -> stringResource(R.string.gender_other)
        "" -> emptyValue()
        else -> raw ?: emptyValue()
    }
}

private fun formatWeightBothLines(
    kg: Double?,
    lbs: Double?,
    unit: UserProfileStore.WeightUnit
): Pair<String, String?> {
    val main = when (unit) {
        UserProfileStore.WeightUnit.KG -> when {
            kg != null -> "${formatWeight1dp(kg)} kg"
            lbs != null -> "${formatWeight1dp(lbs)} lbs"
            else -> "--"
        }

        UserProfileStore.WeightUnit.LBS -> when {
            lbs != null -> "${formatWeight1dp(lbs)} lbs"
            kg != null -> "${formatWeight1dp(kg)} kg"
            else -> "--"
        }
    }

    val sub = when (unit) {
        UserProfileStore.WeightUnit.KG -> lbs?.let { "${formatWeight1dp(it)} lbs" }
        UserProfileStore.WeightUnit.LBS -> kg?.let { "${formatWeight1dp(it)} kg" }
    }

    return main to sub
}

internal fun formatSmartNumber(v: Double): String {
    val isInt = abs(v - v.toInt()) < 1e-9
    return if (isInt) v.toInt().toString() else String.format(Locale.US, "%.1f", v)
}

private fun formatWeight1dp(v: Double): String {
    return String.format(Locale.US, "%.1f", v)
}

private fun emptyValue(): String = "--"

@Composable
private fun PersonalRowDivider() {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 66.dp, end = 16.dp),
        thickness = 1.dp,
        color = BiteCalColors.current().border
    )
}
