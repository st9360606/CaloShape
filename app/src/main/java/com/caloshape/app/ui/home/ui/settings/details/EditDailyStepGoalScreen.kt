package com.caloshape.app.ui.home.ui.settings.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caloshape.app.R
import com.caloshape.app.ui.common.design.CaloShapeColors
import com.caloshape.app.ui.common.design.CaloShapeEditDualActionRow
import com.caloshape.app.ui.common.design.CaloShapeScreenFrame
import com.caloshape.app.ui.common.design.CaloShapeTopBar
import com.caloshape.app.ui.common.haptic.hapticOnFocus
import com.caloshape.app.ui.home.components.HomeBackground
import com.caloshape.app.ui.home.components.HomeCardStyles
import com.caloshape.app.ui.home.ui.settings.details.model.EditDailyStepGoalViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditDailyStepGoalScreen(
    vm: EditDailyStepGoalViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val ui by vm.ui.collectAsState()
    val focus = LocalFocusManager.current
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val screenBackground = if (isDark) Color.Transparent else colors.background
    val cardContainerColor = if (isDark) HomeCardStyles.Surface.card() else colors.surface
    val cardBorderColor = if (isDark) HomeCardStyles.Surface.borderColor() else colors.border
    val primaryTextColor = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
    val secondaryTextColor = if (isDark) HomeCardStyles.Text.secondary() else colors.textSecondary

    LaunchedEffect(Unit) {
        vm.events.collectLatest { e ->
            if (e is EditDailyStepGoalViewModel.UiEvent.Saved) {
                focus.clearFocus()
                onSaved()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDark) {
            HomeBackground(
                modifier = Modifier.fillMaxSize(),
                darkTheme = true,
                enableNoise = false
            )
        }

        Scaffold(
            containerColor = screenBackground,
            topBar = {
                CaloShapeTopBar(
                    title = stringResource(R.string.edit_step_goal_title),
                    onBack = onBack
                )
            }
        ) { inner ->
            Column(
                modifier = Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(horizontal = CaloShapeScreenFrame.contentHorizontalComfort)
                    .padding(top = CaloShapeScreenFrame.detailContentTopNudged, bottom = CaloShapeScreenFrame.detailBottom)
            ) {

                Spacer(Modifier.height(45.dp))

                // --- previous goal card ---
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = cardContainerColor,
                    border = BorderStroke(1.dp, cardBorderColor),
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 17.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StepRingIcon(
                            modifier = Modifier
                                .size(78.dp)
                                .offset(x = 7.dp)
                        )

                        Column {
                            Text(
                                text = ui.previousGoal.toString(),
                                fontSize = 19.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryTextColor,
                                modifier = Modifier.padding(start = 20.dp)
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = stringResource(R.string.edit_daily_step_goal_previous_format, ui.previousGoal),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = secondaryTextColor,
                                modifier = Modifier.padding(start = 20.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(22.dp))

                // --- input box (thick black border, label inside) ---
                StepGoalInputBox(
                    label = stringResource(R.string.edit_daily_step_goal_input_label),
                    value = ui.input,
                    onValueChange = vm::onInputChange,
                    isError = ui.error != null,
                    onImeDone = {
                        if (ui.canSave()) vm.save()
                        focus.clearFocus()
                    }
                )

                Spacer(Modifier.height(22.dp))

                val enabled = ui.canSave()
                CaloShapeEditDualActionRow(
                    secondaryText = stringResource(R.string.common_revert),
                    onSecondaryClick = { vm.revert() },
                    primaryText = stringResource(R.string.common_save),
                    onPrimaryClick = { vm.save() },
                    primaryEnabled = enabled,
                )

                // （圖片沒有顯示錯誤提示；你要 1:1 就先不畫紅字，保留 state 方便之後加）
            }
        }
    }
}

@Composable
private fun StepGoalInputBox(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    onImeDone: () -> Unit
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val border = when {
        isError && isDark -> HomeCardStyles.Status.dangerText()
        isError -> colors.error
        isDark -> HomeCardStyles.Surface.borderColor()
        else -> colors.textPrimary
    }
    val containerColor = if (isDark) HomeCardStyles.Surface.raisedAlt() else Color.Transparent
    val labelColor = if (isDark) HomeCardStyles.Text.secondary() else colors.textSecondary
    val inputColor = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .border(width = if (isDark) 1.2.dp else 2.dp, color = border, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 18.dp)
            .padding(start = 3.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = labelColor
        )
        Spacer(Modifier.height(6.dp))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = inputColor
            ),
            cursorBrush = SolidColor(inputColor),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onImeDone() }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .hapticOnFocus()
        ) { inner ->
            // 讓游標/內容位置看起來更像截圖（左上靠近）
            Box(Modifier.fillMaxWidth()) {
                inner()
            }
        }
    }
}

/**
 * 左側圓環 + 腳印（用 Canvas 自刻，接近截圖）
 * - 灰色全圈 + 黑色右半圈
 * - 中間淡灰底 + 黑腳印
 */
@Composable
private fun StepRingIcon(modifier: Modifier = Modifier) {
    val colors = CaloShapeColors.current()
    val ringGrey = colors.border
    val ringBlack = Color(0xFF73B6E6)
    val innerBg = colors.surfaceMuted

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(
                width = size.minDimension * 0.075f,
                cap = StrokeCap.Round
            )
            val pad = stroke.width / 2f
            val arcSize = Size(size.width - pad * 2, size.height - pad * 2)

            drawArc(
                color = ringGrey,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(pad, pad),
                size = arcSize,
                style = stroke
            )

            drawArc(
                color = ringBlack,
                startAngle = -90f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(pad, pad),
                size = arcSize,
                style = stroke
            )
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(innerBg),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.footstep),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(colors.textPrimary)
            )
        }
    }
}
