package com.caloshape.app.ui.home.ui.card

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caloshape.app.ui.common.haptic.rememberClickWithHaptic
import com.caloshape.app.ui.home.components.CardStyles
import com.caloshape.app.ui.home.components.HomeCardStyles

@Composable
fun FastingPlanCard(
    planName: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
    cardHeight: Dp,
    modifier: Modifier = Modifier,

    planTitle: String = "Fasting Plan",
    startLabel: String = "Start time",
    startText: String? = null,
    endLabel: String = "End time",
    endText: String? = null,

    planNameTextStyle: TextStyle = MaterialTheme.typography.headlineSmall.copy(
        fontWeight = FontWeight.SemiBold
    ),
    planNameFontSize: TextUnit? = null,
    planNameYOffset: Dp = 0.dp,
    planNameXOffset: Dp = 0.dp,
    switchWidth: Dp = 56.dp,
    switchHeight: Dp = 30.dp,

    topBarHeight: Dp = HomeCardStyles.TopBar.Height,
    topBarTextStyle: TextStyle = MaterialTheme.typography.titleSmall,
    topBarPaddingH: Dp = HomeCardStyles.TopBar.HorizontalPadding,

    leftColumnWeight: Float = 1f,
    rightColumnWeight: Float = 0.8f
) {
    val hapticClick = rememberClickWithHaptic(onClick = onClick)
    val isArabic = LocalContext.current.resources.configuration.locales
        .get(0)
        .language
        .equals("ar", ignoreCase = true)
    val timeLabelStyle = if (isArabic) {
        MaterialTheme.typography.labelMedium.copy(
            fontSize = 11.sp,
            lineHeight = 12.sp
        )
    } else {
        MaterialTheme.typography.labelMedium
    }
    val timeValueStyle = if (isArabic) {
        MaterialTheme.typography.bodyMedium.copy(
            fontSize = 15.sp,
            lineHeight = 16.sp,
            textDirection = TextDirection.Ltr
        )
    } else {
        MaterialTheme.typography.bodyMedium.copy(
            textDirection = TextDirection.Ltr
        )
    }
    val timeSpacerHeight = if (isArabic) 2.dp else 6.dp

    Card(
        modifier = modifier
            .height(cardHeight),
        shape = CardStyles.Corner,
        colors = CardDefaults.cardColors(
            containerColor = HomeCardStyles.Surface.card()
        ),
        border = HomeCardStyles.Surface.border(),
        onClick = hapticClick
    ){
        Column(modifier = Modifier.fillMaxSize()) {

            // ===== 上方：黑底白字標題條（固定高度，垂直置中） =====
            Surface(
                color = HomeCardStyles.TopBar.container(),
                contentColor = HomeCardStyles.TopBar.content(),
                shape = HomeCardStyles.TopBar.Shape,
                shadowElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(topBarHeight),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = planTitle,
                        style = topBarTextStyle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = topBarPaddingH)
                    )
                }
            }

            // ===== 內容區：左右分欄（沿用你現在的配置） =====
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左欄：計畫名稱（置中，可 Y 偏移）＋ Switch（置中）
                Column(
                    modifier = Modifier
                        .weight(leftColumnWeight)
                        .fillMaxHeight()
                        .offset(y = (-4).dp),
                    verticalArrangement = Arrangement.spacedBy(
                        space = 15.dp,
                        alignment = Alignment.CenterVertically
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = planName,
                        style = planNameTextStyle,
                        fontSize = planNameFontSize ?: planNameTextStyle.fontSize,
                        color = HomeCardStyles.Text.primary(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(
                                x = planNameXOffset,
                                y = planNameYOffset
                            )
                    )
                    GreenSwitch(
                        checked = enabled,
                        onCheckedChange = onToggle,
                        width = switchWidth,
                        height = switchHeight
                    )
                }

                // 右欄：開始/結束（置中 + 字體較大）
                Column(
                    modifier = Modifier
                        .weight(rightColumnWeight)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = startLabel,
                        style = timeLabelStyle,
                        color = HomeCardStyles.Text.label(),
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = startText ?: "—",
                        style = timeValueStyle,
                        color = HomeCardStyles.Text.primary(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(timeSpacerHeight))
                    Text(
                        text = endLabel,
                        style = timeLabelStyle,
                        color = HomeCardStyles.Text.label(),
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = endText ?: "—",
                        style = timeValueStyle,
                        color = HomeCardStyles.Text.primary(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
