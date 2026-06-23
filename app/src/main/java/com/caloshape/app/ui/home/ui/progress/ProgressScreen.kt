package com.caloshape.app.ui.home.ui.progress

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caloshape.app.R
import com.caloshape.app.ui.common.haptic.caloShapeClickable
import com.caloshape.app.ui.common.bmi.CommonBmiCard
import com.caloshape.app.ui.common.bmi.CommonBmiCardModel
import com.caloshape.app.ui.common.bmi.CommonBmiTone
import com.caloshape.app.ui.home.HomeTab
import com.caloshape.app.ui.home.components.HomeBackground
import com.caloshape.app.ui.home.components.HomeCardStyles
import com.caloshape.app.ui.home.components.MainBottomBar
import com.caloshape.app.ui.common.design.CaloShapeTopBar
import com.caloshape.app.ui.home.ui.progress.model.BmiCardUi
import com.caloshape.app.ui.home.ui.progress.model.BmiStatusTone
import com.caloshape.app.ui.home.ui.progress.model.ProgressViewModel
import com.caloshape.app.ui.common.design.CaloShapeScreenFrame
import com.caloshape.app.ui.common.design.CaloShapeColors

@Composable
fun ProgressScreen(
    vm: ProgressViewModel,
    onBack: () -> Unit,
    onOpenTab: (HomeTab) -> Unit
) {
    val ui by vm.ui.collectAsState()
    var selectedAverageDays by remember { mutableStateOf(7) }
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background

    LaunchedEffect(Unit) { vm.loadIfNeeded() }
    BackHandler { onBack() }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) {
            HomeBackground()
        }

        Scaffold(
            containerColor = if (isDark) Color.Transparent else colors.background,
            topBar = {
                CaloShapeTopBar(
                    title = stringResource(R.string.progress_screen_title),
                    onBack = onBack
                )
            },
            bottomBar = {
                MainBottomBar(current = HomeTab.Progress, onOpenTab = onOpenTab)
            }
        ) { inner ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDark) Color.Transparent else colors.background)
                    .padding(inner),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            item {
                CommonBmiCard(
                    model = rememberProgressBmiCardModel(ui.bmiCard),
                    modifier = Modifier.padding(horizontal = CaloShapeScreenFrame.contentHorizontalCompact)
                )
            }

            item {
                WeekTabs(
                    selected = ui.selectedWeekOffset,
                    onSelect = vm::selectWeek,
                    modifier = Modifier.padding(horizontal = CaloShapeScreenFrame.contentHorizontalCompact)
                )
            }

            when {
                ui.loading -> {
                    item {
                        LoadingCard(
                            modifier = Modifier.padding(horizontal = CaloShapeScreenFrame.contentHorizontalCompact)
                        )
                    }
                }

                ui.error != null -> {
                    item {
                        ErrorCard(
                            message = ui.error?.takeIf { it.isNotBlank() }
                                ?: stringResource(R.string.progress_error_load_failed),
                            onRetry = vm::retry,
                            modifier = Modifier.padding(horizontal = CaloShapeScreenFrame.contentHorizontalCompact)
                        )
                    }
                }

                else -> {
                    item {
                        NutritionChartCard(
                            totalCaloriesText = ui.totalCaloriesText,
                            deltaText = ui.deltaText,
                            average7Calories = ui.average7Calories,
                            average15Calories = ui.average15Calories,
                            days = ui.days,
                            modifier = Modifier.padding(horizontal = CaloShapeScreenFrame.contentHorizontalCompact)
                        )
                    }

                    item {
                        MicronutrientChartCard(
                            days = ui.days,
                            weekOffset = ui.selectedWeekOffset,
                            average7FiberG = ui.average7FiberG,
                            average7SugarG = ui.average7SugarG,
                            average7SodiumMg = ui.average7SodiumMg,
                            modifier = Modifier.padding(horizontal = CaloShapeScreenFrame.contentHorizontalCompact)
                        )
                    }
                }
            }

            item {
                when {
                    ui.workoutLoading && ui.workoutChart.days.isEmpty() -> {
                        WorkoutLoadingCard(
                            modifier = Modifier.padding(horizontal = CaloShapeScreenFrame.contentHorizontalCompact)
                        )
                    }

                    ui.workoutError != null && ui.workoutChart.days.isEmpty() -> {
                        WorkoutErrorCard(
                            message = ui.workoutError?.takeIf { it.isNotBlank() }
                                ?: stringResource(R.string.workout_chart_error_load_failed),
                            onRetry = vm::retryWorkout,
                            modifier = Modifier.padding(horizontal = CaloShapeScreenFrame.contentHorizontalCompact)
                        )
                    }

                    else -> {
                        WorkoutChartCard(
                            chart = ui.workoutChart,
                            modifier = Modifier.padding(horizontal = CaloShapeScreenFrame.contentHorizontalCompact)
                        )
                    }
                }
            }

            item {
                when {
                    ui.waterLoading && ui.waterChart.days.isEmpty() -> {
                        WaterLoadingCard(
                            modifier = Modifier.padding(horizontal = CaloShapeScreenFrame.contentHorizontalCompact)
                        )
                    }

                    ui.waterError != null && ui.waterChart.days.isEmpty() -> {
                        WaterErrorCard(
                            message = ui.waterError?.takeIf { it.isNotBlank() }
                                ?: stringResource(R.string.water_chart_error_load_failed),
                            onRetry = vm::retryWater,
                            modifier = Modifier.padding(horizontal = CaloShapeScreenFrame.contentHorizontalCompact)
                        )
                    }

                    else -> {
                        WaterChartCard(
                            chart = ui.waterChart,
                            modifier = Modifier.padding(horizontal = CaloShapeScreenFrame.contentHorizontalCompact)
                        )
                    }
                }
            }

            item {
                ProgressAverageOverviewSection(
                    selectedDays = selectedAverageDays,
                    onSelectedDaysChange = { selectedAverageDays = it },
                    items = ui.averageOverviewItems,
                    loading = ui.averageOverviewLoading,
                    error = ui.averageOverviewError,
                    onRetry = vm::retryAverageOverview,
                    modifier = Modifier.padding(horizontal = CaloShapeScreenFrame.contentHorizontalCompact)
                )
            }

                item { Spacer(modifier = Modifier.height(5.dp)) }
            }
        }
    }
}

@Composable
private fun WeekTabs(
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val containerBg = if (isDark) HomeCardStyles.Chart.insetSurface() else colors.surfaceMuted
    val activeBg = if (isDark) HomeCardStyles.Chart.surface() else colors.surface
    val activeBorder = if (isDark) HomeCardStyles.Chart.border() else colors.border
    val activeText = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
    val idleText = if (isDark) HomeCardStyles.Text.secondary() else colors.textSecondary
    val labels = listOf(
        stringResource(R.string.progress_tab_this_week),
        stringResource(R.string.progress_tab_last_week),
        stringResource(R.string.progress_tab_two_weeks_ago),
        stringResource(R.string.progress_tab_three_weeks_ago)
    )
    val containerShape = RoundedCornerShape(18.dp)
    val activeTabShape = RoundedCornerShape(14.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(containerBg, containerShape)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        labels.forEachIndexed { index, label ->
            val active = index == selected
            val interactionSource = remember { MutableInteractionSource() }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .then(
                        if (active) {
                            Modifier
                                .shadow(
                                    elevation = 1.5.dp,
                                    shape = activeTabShape,
                                    clip = false
                                )
                                .background(activeBg, activeTabShape)
                                .border(1.dp, activeBorder, activeTabShape)
                        } else {
                            Modifier.background(Color.Transparent, activeTabShape)
                        }
                    )
                    .caloShapeClickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onSelect(index) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (active) activeText else idleText,
                    fontSize = 14.sp,
                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun rememberProgressBmiCardModel(
    ui: BmiCardUi
): CommonBmiCardModel {
    val title = stringResource(R.string.bmi_card_title)
    val subtitle = stringResource(R.string.bmi_card_subtitle)
    val underweight = stringResource(R.string.bmi_status_underweight)
    val healthy = stringResource(R.string.bmi_status_healthy)
    val overweight = stringResource(R.string.bmi_status_overweight)
    val obese = stringResource(R.string.bmi_status_obese)
    val unknown = stringResource(R.string.bmi_status_unknown)

    return remember(
        ui.bmiText,
        ui.statusTone,
        ui.markerProgress,
        title,
        subtitle,
        underweight,
        healthy,
        overweight,
        obese,
        unknown
    ) {
        val localizedStatus = when (ui.statusTone) {
            BmiStatusTone.Underweight -> underweight
            BmiStatusTone.Healthy -> healthy
            BmiStatusTone.Overweight -> overweight
            BmiStatusTone.Obese -> obese
            BmiStatusTone.Unknown -> unknown
        }

        CommonBmiCardModel(
            bmiText = ui.bmiText,
            statusText = localizedStatus,
            statusTone = ui.statusTone.toCommonBmiTone(),
            markerProgress = ui.markerProgress,
            titleText = title,
            subtitleText = subtitle
        )
    }
}

private fun BmiStatusTone.toCommonBmiTone(): CommonBmiTone {
    return when (this) {
        BmiStatusTone.Underweight -> CommonBmiTone.Underweight
        BmiStatusTone.Healthy -> CommonBmiTone.Healthy
        BmiStatusTone.Overweight -> CommonBmiTone.Overweight
        BmiStatusTone.Obese -> CommonBmiTone.Obese
        BmiStatusTone.Unknown -> CommonBmiTone.Unknown
    }
}
