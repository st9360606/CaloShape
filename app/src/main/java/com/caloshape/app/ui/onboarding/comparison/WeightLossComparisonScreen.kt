package com.caloshape.app.ui.onboarding.comparison

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caloshape.app.R
import com.caloshape.app.ui.common.design.CaloShapeOnboardingBottomBar
import com.caloshape.app.ui.common.design.CaloShapeOnboardingColors
import com.caloshape.app.ui.common.design.CaloShapeOnboardingTopBar
import com.caloshape.app.ui.common.design.CaloShapeScreenFrame

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightLossComparisonScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = CaloShapeOnboardingColors.background(),
        topBar = {
            CaloShapeOnboardingTopBar(
                stepIndex = 9,
                totalSteps = 12,
                onBack = onBack
            )
        },
        bottomBar = {
            CaloShapeOnboardingBottomBar(
                primaryText = stringResource(R.string.common_continue_btn),
                onPrimaryClick = onNext
            )
        },
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            horizontalAlignment = Alignment.Start
        ) {

            Spacer(Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.onboard_weight_loss_comparison_title),
                color = CaloShapeOnboardingColors.title(),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 38.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 36.dp, end = 24.dp),
                textAlign = TextAlign.Start
            )

            Spacer(Modifier.height(42.dp))

            ComparisonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = CaloShapeScreenFrame.contentHorizontalExtraWide)
            )
        }
    }
}

@Composable
private fun ComparisonCard(
    modifier: Modifier = Modifier,
) {
    val isDark = CaloShapeOnboardingColors.isDark()
    val cardShape = RoundedCornerShape(28.dp)
    val cardBorderColor = if (isDark) CaloShapeOnboardingColors.softBorder() else Color.Transparent
    val cardBrush = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                CaloShapeOnboardingColors.cardSurface(),
                Color(0xFF1E1B24),
                Color(0xFF24212D)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFF4F5F5),
                Color(0xFFF4F4F4),
                Color(0xFFFFF4F9)
            )
        )
    }

    Box(
        modifier = modifier
            .height(380.dp)
            .clip(cardShape)
            .background(cardBrush)
            .border(width = if (isDark) 1.2.dp else 0.dp, color = cardBorderColor, shape = cardShape)
            .padding(horizontal = 28.dp, vertical = 42.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                ComparisonPillar(
                    title = stringResource(R.string.onboard_weight_loss_comparison_without_ai),
                    value = stringResource(R.string.onboard_weight_loss_comparison_twenty_percent),
                    valueContainerColor = if (isDark) Color(0xFF4A4558) else Color(0xFFE1E1E1),
                    valueTextColor = if (isDark) CaloShapeOnboardingColors.title() else Color(0xFF111114),
                    bottomBlockHeight = 51.dp
                )

                Spacer(Modifier.width(36.dp))

                ComparisonPillar(
                    title = stringResource(R.string.onboard_weight_loss_comparison_with_ai),
                    value = stringResource(R.string.onboard_weight_loss_comparison_two_times),
                    valueContainerColor = if (isDark) Color(0xFFF7F5FF) else Color(0xFF1C1822),
                    valueTextColor = if (isDark) Color(0xFF111114) else Color.White,
                    bottomBlockHeight = 122.dp
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.onboard_weight_loss_comparison_caption),
                color = if (isDark) CaloShapeOnboardingColors.subtitle() else Color(0xFF57575D),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ComparisonPillar(
    title: String,
    value: String,
    valueContainerColor: Color,
    valueTextColor: Color,
    bottomBlockHeight: Dp,
    modifier: Modifier = Modifier,
) {
    val pillarBg = if (CaloShapeOnboardingColors.isDark()) {
        Color(0xFF24212D)
    } else {
        Color.White
    }
    val titleColor = CaloShapeOnboardingColors.title()
    val pillarBorderColor = if (CaloShapeOnboardingColors.isDark()) {
        CaloShapeOnboardingColors.softBorder()
    } else {
        Color.Transparent
    }

    Column(
        modifier = modifier
            .width(104.dp)
            .height(202.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(pillarBg)
            .border(
                width = if (CaloShapeOnboardingColors.isDark()) 1.dp else 0.dp,
                color = pillarBorderColor,
                shape = RoundedCornerShape(18.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = title,
            color = titleColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 14.dp)
                .weight(1f),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomBlockHeight)
                .clip(RoundedCornerShape(18.dp))
                .background(valueContainerColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                color = valueTextColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
