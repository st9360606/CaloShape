package com.caloshape.app.ui.home.ui.progress

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caloshape.app.R
import com.caloshape.app.ui.common.design.CaloShapeColors
import com.caloshape.app.ui.common.haptic.caloShapeClickable
import com.caloshape.app.ui.home.components.HomeCardStyles
import com.caloshape.app.ui.home.ui.progress.model.WaterChartUi
import com.caloshape.app.ui.home.ui.progress.model.WaterProgressDayUi
import com.caloshape.app.ui.home.ui.progress.tooltip.ChartTooltipCard
import com.caloshape.app.ui.home.ui.progress.tooltip.ChartTooltipMetricUi
import com.caloshape.app.ui.home.ui.progress.tooltip.ChartTooltipPressState
import com.caloshape.app.ui.home.ui.progress.tooltip.calculateChartTooltipOffsetPx
import com.caloshape.app.ui.home.ui.progress.tooltip.chartTooltipPressTarget
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

private val WaterBarColor = Color(0xFF73B6E6)
private val WaterGoalLineColor = Color(0xFF3C9E45)

private val WaterFooterReachedBg = Color(0xFFEAF5E8)
private val WaterFooterReachedText = Color(0xFF3C9E45)
private val WaterFooterPendingBg = Color(0xFFEEF5FF)
private val WaterFooterPendingText = WaterBarColor

@Composable
internal fun WaterChartCard(
    chart: WaterChartUi,
    modifier: Modifier = Modifier
) {
    val footerText = stringResource(R.string.water_chart_encouragement)

    WaterChartCardFrame(
        title = stringResource(R.string.water_chart_title),
        headlineValue = chart.averageSelectedWeekMl?.let(::formatMlPlain),
        unitText = stringResource(R.string.water_chart_unit_ml),
        goalText = stringResource(R.string.water_chart_goal),
        goalValue = stringResource(
            R.string.water_chart_value_ml,
            formatMlPlain(chart.goalMl)
        ),
        avgText = stringResource(R.string.water_chart_7day_avg),
        avgValue = stringResource(
            R.string.water_chart_value_ml,
            formatMlPlain(chart.averageMl)
        ),
        footerText = footerText,
        footerBackground = if (chart.reachedAverageGoal) WaterFooterReachedBg else WaterFooterPendingBg,
        footerTextColor = if (chart.reachedAverageGoal) WaterFooterReachedText else WaterFooterPendingText,
        modifier = modifier
    ) {
        WaterBarChart(
            days = chart.days,
            goalMl = chart.goalMl,
            showBars = true
        )
    }
}

@Composable
internal fun WaterLoadingCard(
    modifier: Modifier = Modifier
) {
    val colors = CaloShapeColors.current()

    WaterChartCardFrame(
        title = stringResource(R.string.water_chart_title),
        headlineValue = "--",
        unitText = stringResource(R.string.water_chart_unit_ml),
        goalText = stringResource(R.string.water_chart_goal),
        goalValue = "--",
        avgText = stringResource(R.string.water_chart_7day_avg),
        avgValue = "--",
        footerText = stringResource(R.string.water_chart_loading),
        footerBackground = colors.surfaceMuted,
        footerTextColor = colors.textSecondary,
        modifier = modifier
    ) {
        WaterBarChart(
            days = emptyList(),
            goalMl = 2000,
            showBars = false
        )
    }
}

@Composable
internal fun WaterErrorCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isDark) HomeCardStyles.Chart.surface() else colors.surface,
                RoundedCornerShape(28.dp)
            )
            .border(
                1.dp,
                if (isDark) HomeCardStyles.Chart.border() else colors.border,
                RoundedCornerShape(28.dp)
            )
            .padding(horizontal = 26.dp, vertical = 26.dp)
    ) {
        Text(
            text = stringResource(R.string.water_chart_title),
            color = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(28.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                color = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .background(colors.primaryButtonContainer, RoundedCornerShape(999.dp))
                    .caloShapeClickable { onRetry() }
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.common_retry),
                    color = colors.primaryButtonContent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun WaterChartCardFrame(
    title: String,
    headlineValue: String?,
    unitText: String,
    goalText: String,
    goalValue: String,
    avgText: String,
    avgValue: String,
    footerText: String?,
    footerBackground: Color,
    footerTextColor: Color,
    modifier: Modifier = Modifier,
    chartContent: @Composable () -> Unit
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val resolvedFooterBackground = if (isDark) {
        HomeCardStyles.Chart.footerSurface()
    } else {
        footerBackground
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isDark) HomeCardStyles.Chart.surface() else colors.surface,
                RoundedCornerShape(28.dp)
            )
            .border(
                1.dp,
                if (isDark) HomeCardStyles.Chart.border() else colors.border,
                RoundedCornerShape(28.dp)
            )
    ) {
        val metricChipWidth = 102.dp

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(start = 26.dp, top = 26.dp, end = 26.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                    Text(
                        text = title,
                        color = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 10.dp)
                    )

                    Text(
                        text = stringResource(R.string.progress_chart_average_label),
                        color = Color(0xFFF43F5E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (headlineValue == null) {
                        Text(
                            text = stringResource(R.string.progress_chart_no_records),
                            color = if (isDark) HomeCardStyles.Text.secondary() else colors.textSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 20.sp
                        )
                    } else Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = headlineValue,
                            color = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 36.sp
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = unitText,
                            color = if (isDark) HomeCardStyles.Text.secondary() else colors.textSecondary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.offset(x = 12.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WaterMetricChip(
                            label = goalText,
                            value = goalValue,
                            accentColor = WaterGoalLineColor,
                            modifier = Modifier.width(metricChipWidth)
                        )

                        WaterMetricChip(
                            label = avgText,
                            value = avgValue,
                            accentColor = WaterBarColor,
                            modifier = Modifier.width(metricChipWidth)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                chartContent()

                Spacer(modifier = Modifier.height(18.dp))

                WaterLegendRow()
            }

            footerText?.let { text ->
                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .background(
                            color = resolvedFooterBackground,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = text,
                        color = footerTextColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}
@Composable
private fun WaterMetricChip(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background

    Row(
        modifier = modifier
            .background(
                if (isDark) HomeCardStyles.Chart.insetSurface() else colors.surfaceMuted,
                RoundedCornerShape(14.dp)
            )
            .border(
                1.dp,
                if (isDark) HomeCardStyles.Chart.border() else colors.border,
                RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(26.dp)
                .background(accentColor.copy(alpha = 0.86f), RoundedCornerShape(999.dp))
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = label,
                color = if (isDark) HomeCardStyles.Text.secondary() else colors.textSecondary,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                color = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
@Composable
private fun WaterLegendRow() {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(12.dp)
                    .background(WaterBarColor, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.water_legend_intake),
                color = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(
                modifier = Modifier
                    .width(18.dp)
                    .height(10.dp)
            ) {
                drawLine(
                    color = WaterGoalLineColor,
                    start = Offset(0f, size.height / 2f),
                    end = Offset(size.width, size.height / 2f),
                    strokeWidth = 3.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.water_legend_daily_goal),
                color = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun WaterBarChart(
    days: List<WaterProgressDayUi>,
    goalMl: Int,
    showBars: Boolean = true
) {
    val orderedLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val dayMap = days.associateBy { it.dayLabel.take(3) }

    val chartDays = orderedLabels.map { label ->
        dayMap[label] ?: WaterProgressDayUi(
            date = "",
            dayLabel = label,
            ml = 0
        )
    }

    val rawMax = maxOf(
        goalMl.toFloat(),
        chartDays.maxOfOrNull { it.ml.toFloat() } ?: 0f
    )

    val yAxisMax = computeWaterAxisMax(rawMax)
    val yTicks = buildYAxisTicks(yAxisMax, segments = 4)

    val chartAreaHeight = 184.dp
    val chartRowHeight = 184.dp
    val xAxisGap = 8.dp
    val yAxisWidth = 36.dp
    val yAxisToChartGap = 2.dp
    val plotTopInset = 10.dp
    val plotBottomInset = 0.dp
    val yLabelHalfHeight = 9.dp
    val plotHeightDp = chartAreaHeight - plotTopInset - plotBottomInset
    val plotEndPadding = 8.dp

    val animProgressList = remember {
        List(7) { Animatable(0f) }
    }

    var pressedTooltip by remember(chartDays, showBars) {
        mutableStateOf<ChartTooltipPressState<WaterProgressDayUi>?>(null)
    }

    var chartSizePx by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(chartDays.map { it.ml }, showBars, goalMl) {
        pressedTooltip = null

        chartDays.forEachIndexed { index, day ->
            launch {
                val target = if (!showBars || yAxisMax <= 0f || day.ml <= 0) {
                    0f
                } else {
                    (day.ml / yAxisMax).coerceIn(0f, 1f)
                }

                animProgressList[index].animateTo(
                    targetValue = target,
                    animationSpec = tween(
                        durationMillis = 320,
                        delayMillis = index * 24,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartRowHeight),
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier
                    .width(yAxisWidth)
                    .height(chartAreaHeight)
            ) {
                yTicks.asReversed().forEach { tick ->
                    val ratio = if (yAxisMax == 0f) 0f else tick / yAxisMax
                    val tickY = chartAreaHeight - plotBottomInset - (plotHeightDp * ratio)

                    Text(
                        text = tick.roundToInt().toString(),
                        color = ProgressChartAxisDefaults.idleLabelColor(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(
                                x = (-8).dp,
                                y = tickY - yLabelHalfHeight
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.width(yAxisToChartGap))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(chartAreaHeight)
                    .padding(end = plotEndPadding)
                    .onSizeChanged { chartSizePx = it }
            ) {
                val density = LocalDensity.current
                var tooltipSizePx by remember { mutableStateOf(IntSize.Zero) }

                val tooltipMinWidth = 124.dp
                val slotCount = if (chartDays.isEmpty()) 7 else chartDays.size

                val chartWidthPx = chartSizePx.width.takeIf { it > 0 }?.toFloat() ?: 1f
                val chartHeightPx = chartSizePx.height.takeIf { it > 0 }?.toFloat() ?: 1f
                val slotWidthPx = chartWidthPx / slotCount.toFloat()

                val fallbackTooltipWidthPx = with(density) { tooltipMinWidth.toPx() }
                val fallbackTooltipHeightPx = with(density) { 86.dp.toPx() }

                val resolvedTooltipWidthPx = tooltipSizePx.width.takeIf { it > 0 }?.toFloat()
                    ?: fallbackTooltipWidthPx

                val resolvedTooltipHeightPx = tooltipSizePx.height.takeIf { it > 0 }?.toFloat()
                    ?: fallbackTooltipHeightPx

                val tooltipGapXPx = with(density) { 18.dp.toPx() }
                val tooltipFixedTopPx = with(density) { (-18).dp.toPx() }
                val tooltipEdgePaddingPx = with(density) { 4.dp.toPx() }
                val gridLineColor = ProgressChartAxisDefaults.gridColor()

                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    val strokeWidth = 2f

                    val plotTop = plotTopInset.toPx()
                    val plotBottom = size.height - plotBottomInset.toPx()
                    val plotHeight = (plotBottom - plotTop).coerceAtLeast(0f)

                    yTicks.forEach { tick ->
                        val ratio = if (yAxisMax == 0f) 0f else tick / yAxisMax
                        val y = plotBottom - (ratio * plotHeight)

                        drawLine(
                            color = gridLineColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth,
                            pathEffect = dash
                        )
                    }

                    val goalRatio = if (yAxisMax == 0f) 0f else goalMl / yAxisMax
                    val goalY = plotBottom - (goalRatio * plotHeight)

                    drawLine(
                        color = WaterGoalLineColor,
                        start = Offset(0f, goalY),
                        end = Offset(size.width, goalY),
                        strokeWidth = 3f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    chartDays.forEachIndexed { index, day ->
                        val animatedProgress = animProgressList[index].value
                        val hasData = day.ml > 0

                        val barPressModifier =
                            Modifier.chartTooltipPressTarget(
                                enabled = showBars,
                                index = index,
                                payload = day,
                                onTooltipChange = { pressedTooltip = it }
                            )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .then(barPressModifier),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .width(34.dp)
                                    .fillMaxHeight()
                            ) {
                                val plotTop = plotTopInset.toPx()
                                val plotBottom = size.height - plotBottomInset.toPx()
                                val plotHeight = (plotBottom - plotTop).coerceAtLeast(0f)

                                if (!showBars) return@Canvas
                                if (!hasData || animatedProgress <= 0f || yAxisMax <= 0f) return@Canvas

                                val barWidth = size.width * 0.84f
                                val left = (size.width - barWidth) / 2f
                                val barHeight = (plotHeight * animatedProgress).coerceAtLeast(0f)
                                val top = plotBottom - barHeight

                                val resolvedRadius = minOf(
                                    8.dp.toPx(),
                                    barWidth / 2f,
                                    barHeight / 2f
                                )

                                val path = Path().apply {
                                    addRoundRect(
                                        RoundRect(
                                            left = left,
                                            top = top,
                                            right = left + barWidth,
                                            bottom = top + barHeight,
                                            topLeftCornerRadius = CornerRadius(resolvedRadius, resolvedRadius),
                                            topRightCornerRadius = CornerRadius(resolvedRadius, resolvedRadius),
                                            bottomRightCornerRadius = CornerRadius(0f, 0f),
                                            bottomLeftCornerRadius = CornerRadius(0f, 0f)
                                        )
                                    )
                                }

                                drawPath(
                                    path = path,
                                    color = WaterBarColor,
                                    style = Fill
                                )
                            }
                        }
                    }
                }

                pressedTooltip?.let { tooltip ->
                    val tooltipOffset = calculateChartTooltipOffsetPx(
                        chartWidthPx = chartWidthPx,
                        chartHeightPx = chartHeightPx,
                        slotWidthPx = slotWidthPx,
                        tooltipWidthPx = resolvedTooltipWidthPx,
                        tooltipHeightPx = resolvedTooltipHeightPx,
                        tooltipIndex = tooltip.index,
                        pressOffsetInSlotPx = tooltip.pressOffsetInSlotPx,
                        horizontalGapPx = tooltipGapXPx,
                        fixedTopPx = tooltipFixedTopPx,
                        edgePaddingPx = tooltipEdgePaddingPx
                    )

                    WaterDayTooltip(
                        day = tooltip.payload,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset { tooltipOffset }
                            .onGloballyPositioned { coordinates ->
                                val newSize = coordinates.size
                                if (tooltipSizePx != newSize) {
                                    tooltipSizePx = newSize
                                }
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(xAxisGap))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = yAxisWidth + yAxisToChartGap,
                    end = plotEndPadding
                )
        ) {
            chartDays.forEach { day ->
                val isToday = ProgressChartAxisDefaults.isToday(
                    dateIso = day.date,
                    dayLabel = day.dayLabel
                )

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = localizedWaterDayLabel(day.dayLabel),
                        color = if (isToday) {
                            ProgressChartAxisDefaults.todayLabelColor()
                        } else {
                            ProgressChartAxisDefaults.idleLabelColor()
                        },
                        fontSize = 13.sp,
                        fontWeight = if (isToday) {
                            ProgressChartAxisDefaults.TodayLabelWeight
                        } else {
                            ProgressChartAxisDefaults.IdleLabelWeight
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun WaterDayTooltip(
    day: WaterProgressDayUi,
    modifier: Modifier = Modifier
) {
    val intakeLabel = stringResource(R.string.water_tooltip_intake)

    ChartTooltipCard(
        metrics = listOf(
            ChartTooltipMetricUi(
                emoji = "💧",
                label = stringResource(R.string.progress_tooltip_label_format, intakeLabel),
                value = stringResource(R.string.water_tooltip_ml_value, day.ml)
            )
        ),
        dayLabel = localizedWaterDayLabel(day.dayLabel),
        modifier = modifier,
        maxWidth = 196.dp
    )
}

private fun computeWaterAxisMax(rawMax: Float): Float {
    if (rawMax <= 0f) return 2000f

    val roughStep = rawMax / 4f
    val magnitude = 10.0.pow(floor(log10(roughStep.toDouble()))).toFloat()
    val residual = roughStep / magnitude

    val niceStep = when {
        residual <= 1f -> 1f
        residual <= 2f -> 2f
        residual <= 5f -> 5f
        else -> 10f
    } * magnitude

    return ceil(rawMax / niceStep).toFloat() * niceStep
}

private fun buildYAxisTicks(
    max: Float,
    segments: Int
): List<Float> {
    return (0..segments).map { index ->
        max * index / segments.toFloat()
    }
}

private fun formatMlPlain(value: Int): String {
    return value.toString()
}

@Composable
private fun localizedWaterDayLabel(label: String): String {
    return when (label.take(3)) {
        "Sun" -> stringResource(R.string.progress_day_sun)
        "Mon" -> stringResource(R.string.progress_day_mon)
        "Tue" -> stringResource(R.string.progress_day_tue)
        "Wed" -> stringResource(R.string.progress_day_wed)
        "Thu" -> stringResource(R.string.progress_day_thu)
        "Fri" -> stringResource(R.string.progress_day_fri)
        "Sat" -> stringResource(R.string.progress_day_sat)
        else -> label
    }
}

