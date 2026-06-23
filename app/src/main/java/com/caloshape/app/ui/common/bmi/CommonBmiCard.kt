package com.caloshape.app.ui.common.bmi

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caloshape.app.R
import com.caloshape.app.ui.common.design.CaloShapeColors
import com.caloshape.app.ui.common.haptic.caloShapeClickable
import com.caloshape.app.ui.home.components.HomeCardStyles

enum class CommonBmiTone {
    Underweight,
    Healthy,
    Overweight,
    Obese,
    Unknown
}

data class CommonBmiCardModel(
    val bmiText: String = "--.--",
    val statusText: String = "--",
    val statusTone: CommonBmiTone = CommonBmiTone.Unknown,
    val markerProgress: Float = 0.5f,
    val titleText: String = "",
    val subtitleText: String = ""
)

private val UnknownPill = Color(0xFFB8BDC7)

private val BarBlue = Color(0xFF2D9CDB)
private val BarGreen = Color(0xFF35C36C)
private val BarYellow = Color(0xFFF2C94C)
private val BarOrange = Color(0xFFF2994A)
private val BarRed = Color(0xFFEB5757)
@Composable
fun CommonBmiCard(
    model: CommonBmiCardModel,
    modifier: Modifier = Modifier
) {
    var showBmiInfoDialog by rememberSaveable { mutableStateOf(false) }
    val dialogModel = rememberCommonBmiInfoDialogModel()
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val cardContainer = if (isDark) HomeCardStyles.Chart.surface() else colors.surface
    val cardBorder = if (isDark) HomeCardStyles.Chart.border() else colors.border
    val primaryText = if (isDark) HomeCardStyles.Text.primary() else colors.textPrimary
    val secondaryText = if (isDark) HomeCardStyles.Text.secondary() else colors.textSecondary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(cardContainer, RoundedCornerShape(28.dp))
            .border(1.dp, cardBorder, RoundedCornerShape(28.dp))
            .padding(start = 22.dp, top = 22.dp, end = 22.dp, bottom = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = model.titleText,
                color = primaryText,
                fontSize = 24.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .offset(x = 6.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .caloShapeClickable { showBmiInfoDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                    contentDescription = stringResource(R.string.bmi_info_title),
                    tint = secondaryText,
                    modifier = Modifier.size(23.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = model.bmiText,
                    color = primaryText,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 35.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = model.subtitleText,
                    color = secondaryText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    modifier = Modifier.widthIn(min = 92.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                CommonBmiStatusPill(
                    text = model.statusText,
                    tone = model.statusTone
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        CommonBmiRangeBar(
            markerProgress = model.markerProgress
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            CommonBmiLegendItem(
                color = BarBlue,
                label = stringResource(R.string.bmi_status_underweight),
                rangeText = stringResource(R.string.bmi_range_underweight)
            )
            CommonBmiLegendItem(
                color = BarGreen,
                label = stringResource(R.string.bmi_status_healthy),
                rangeText = stringResource(R.string.bmi_range_healthy)
            )
            CommonBmiLegendItem(
                color = BarYellow,
                label = stringResource(R.string.bmi_status_overweight),
                rangeText = stringResource(R.string.bmi_range_overweight)
            )
            CommonBmiLegendItem(
                color = BarRed,
                label = stringResource(R.string.bmi_status_obese),
                rangeText = stringResource(R.string.bmi_range_obese)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showBmiInfoDialog) {
        CommonBmiInfoDialog(
            model = dialogModel,
            onDismiss = { showBmiInfoDialog = false }
        )
    }
}

@Composable
private fun rememberCommonBmiInfoDialogModel(): CommonBmiInfoDialogModel {
    val title = stringResource(R.string.bmi_info_title)
    val subtitle = stringResource(R.string.bmi_info_subtitle)
    val formulaTitle = stringResource(R.string.bmi_info_formula_title)
    val formulaValue = stringResource(R.string.bmi_info_formula_value)
    val meaningTitle = stringResource(R.string.bmi_info_meaning_title)
    val meaningBody = stringResource(R.string.bmi_info_meaning_body)
    val underweight = stringResource(R.string.bmi_status_underweight)
    val healthy = stringResource(R.string.bmi_status_healthy)
    val overweight = stringResource(R.string.bmi_status_overweight)
    val obese = stringResource(R.string.bmi_status_obese)
    val noteTitle = stringResource(R.string.bmi_info_note_title)
    val noteBody = stringResource(R.string.bmi_info_note_body)
    val cta = stringResource(R.string.bmi_info_cta)

    return remember(
        title,
        subtitle,
        formulaTitle,
        formulaValue,
        meaningTitle,
        meaningBody,
        underweight,
        healthy,
        overweight,
        obese,
        noteTitle,
        noteBody,
        cta
    ) {
        CommonBmiInfoDialogModel(
            title = title,
            subtitle = subtitle,
            formulaTitle = formulaTitle,
            formulaValue = formulaValue,
            meaningTitle = meaningTitle,
            meaningBody = meaningBody,
            underweightText = underweight,
            healthyText = healthy,
            overweightText = overweight,
            obeseText = obese,
            noteTitle = noteTitle,
            noteBody = noteBody,
            ctaText = cta
        )
    }
}

@Composable
private fun CommonBmiStatusPill(
    text: String,
    tone: CommonBmiTone
) {
    val bg = when (tone) {
        CommonBmiTone.Underweight -> BarBlue
        CommonBmiTone.Healthy -> BarGreen
        CommonBmiTone.Overweight -> BarYellow
        CommonBmiTone.Obese -> BarRed
        CommonBmiTone.Unknown -> UnknownPill
    }

    Box(
        modifier = Modifier
            .widthIn(min = 64.dp, max = 136.dp)
            .heightIn(min = 24.dp)
            .background(bg, RoundedCornerShape(999.dp))
            .padding(horizontal = 11.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun CommonBmiRangeBar(
    markerProgress: Float,
    modifier: Modifier = Modifier
) {
    val colors = CaloShapeColors.current()
    val clamped = markerProgress.coerceIn(0f, 1f)
    val markerWidth = 3.dp
    val markerHeight = 18.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0.00f to BarBlue,
                            0.34f to BarGreen,
                            0.64f to BarYellow,
                            0.82f to BarOrange,
                            1.00f to BarRed
                        )
                    ),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (maxWidth - markerWidth) * clamped)
                .width(markerWidth)
                .height(markerHeight)
                .background(colors.textPrimary, RoundedCornerShape(999.dp))
        )
    }
}

@Composable
private fun CommonBmiLegendItem(
    color: Color,
    label: String,
    rangeText: String,
    modifier: Modifier = Modifier
) {
    val colors = CaloShapeColors.current()
    val isDark = colors.background == CaloShapeColors.Dark.background
    val labelText = if (isDark) HomeCardStyles.Text.secondary() else colors.textSecondary
    val rangeLabelText = if (isDark) HomeCardStyles.Text.muted() else colors.textMuted
    val dotSize = 8.dp
    val dotGap = 4.dp

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(dotSize)
                .background(color, CircleShape)
        )

        Spacer(modifier = Modifier.width(dotGap))

        Column {
            Text(
                text = label,
                color = labelText,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Clip
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = rangeText,
                color = rangeLabelText,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
    }
}
