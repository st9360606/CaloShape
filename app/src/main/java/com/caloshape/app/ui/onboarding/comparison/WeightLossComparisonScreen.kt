package com.caloshape.app.ui.onboarding.comparison

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = CaloShapeOnboardingColors.background(),
        topBar = {
            CaloShapeOnboardingTopBar(
                stepIndex = 9,
                totalSteps = 12,
                onBack = onBack,
            )
        },
        bottomBar = {
            CaloShapeOnboardingBottomBar(
                primaryText = stringResource(R.string.common_continue_btn),
                onPrimaryClick = onNext,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.onboard_weight_loss_comparison_title),
                color = CaloShapeOnboardingColors.title(),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 38.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 36.dp, end = 24.dp),
            )

            Spacer(modifier = Modifier.height(28.dp))

            ComparisonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = CaloShapeScreenFrame.contentHorizontalExtraWide),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ComparisonCard(
    modifier: Modifier = Modifier,
) {
    val isDark = CaloShapeOnboardingColors.isDark()
    val withoutAiItems = listOf(
        stringResource(R.string.onboard_weight_loss_without_ai_item_1),
        stringResource(R.string.onboard_weight_loss_without_ai_item_2),
        stringResource(R.string.onboard_weight_loss_without_ai_item_3),
        stringResource(R.string.onboard_weight_loss_without_ai_item_4),
        stringResource(R.string.onboard_weight_loss_without_ai_item_5),
    )
    val withAiItems = listOf(
        stringResource(R.string.onboard_weight_loss_with_ai_item_1),
        stringResource(R.string.onboard_weight_loss_with_ai_item_2),
        stringResource(R.string.onboard_weight_loss_with_ai_item_3),
        stringResource(R.string.onboard_weight_loss_with_ai_item_4),
        stringResource(R.string.onboard_weight_loss_with_ai_item_5),
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            ComparisonFeaturePanel(
                title = stringResource(R.string.onboard_weight_loss_comparison_without_ai),
                items = withoutAiItems,
                highlighted = false,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )

            ComparisonFeaturePanel(
                title = stringResource(R.string.onboard_weight_loss_comparison_with_ai),
                items = withAiItems,
                highlighted = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.onboard_weight_loss_comparison_caption),
            color = if (isDark) CaloShapeOnboardingColors.subtitle() else Color(0xFF57575D),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 21.sp,
            textAlign = TextAlign.Center,
            maxLines = 3,
            modifier = Modifier.widthIn(max = 300.dp),
        )
    }
}

@Composable
private fun ComparisonFeaturePanel(
    title: String,
    items: List<String>,
    highlighted: Boolean,
    modifier: Modifier = Modifier,
) {
    val isDark = CaloShapeOnboardingColors.isDark()
    val shape = RoundedCornerShape(22.dp)
    val surfaceColor = when {
        highlighted && isDark -> Color(0xFF2D2933)
        highlighted -> Color(0xFFF3F0F5)
        isDark -> Color(0xFF24212D)
        else -> Color.White
    }
    val borderColor = when {
        highlighted && isDark -> Color(0xFF4A4554)
        highlighted -> Color(0xFFDED7E2)
        isDark -> CaloShapeOnboardingColors.softBorder()
        else -> Color(0xFFE7E3E8)
    }
    val titleColor = if (isDark) Color(0xFFF7F5FF) else Color(0xFF1C1822)
    val itemColor = if (highlighted) titleColor else CaloShapeOnboardingColors.subtitle()

    Column(
        modifier = modifier
            .clip(shape)
            .background(surfaceColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape,
            )
            .padding(horizontal = 12.dp, vertical = 14.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = title,
                color = titleColor,
                fontSize = 15.sp,
                fontWeight = if (highlighted) FontWeight.ExtraBold else FontWeight.Bold,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(borderColor),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items.forEach { item ->
                ComparisonFeatureItem(
                    text = item,
                    highlighted = highlighted,
                    contentColor = itemColor,
                )
            }
        }
    }
}

@Composable
private fun ComparisonFeatureItem(
    text: String,
    highlighted: Boolean,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.Top,
    ) {
        if (highlighted) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(11.dp),
                )
            }
        }

        Text(
            text = text,
            color = contentColor,
            fontSize = 13.sp,
            fontWeight = if (highlighted) FontWeight.SemiBold else FontWeight.Medium,
            lineHeight = 17.sp,
            modifier = Modifier.weight(1f),
        )
    }
}
