package com.caloshape.app.ui.home.ui.camera.menu

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caloshape.app.R
import com.caloshape.app.ui.common.haptic.caloShapeClickable
import com.caloshape.app.ui.home.components.HomeCardStyles
import com.caloshape.app.ui.home.components.CardStyles
import com.caloshape.app.ui.home.ui.camera.scan.ScanCameraIcon
import com.caloshape.app.ui.common.haptic.rememberClickWithHaptic

private val ScrimColor = Color.Black.copy(alpha = 0.16f)
private val DarkMenuCardColor = Color(0xFF3A3544)
private val LightTileColor = Color(0xFFF0F1F6)
private val DarkTileColor = Color.White.copy(alpha = 0.14f)
private val LightContentColor = Color(0xFF202124)
private val DarkContentColor = Color.White

private val MenuSidePadding = 20.dp
private val MenuBaseBottomPadding = 78.dp
private val MenuCardSpacing = 12.dp

@Composable
fun HomeQuickActionMenu(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSavedFoodsClick: () -> Unit,
    onScanFoodClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    BackHandler(enabled = visible, onBack = onDismiss)

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("quick_add_menu")
    ) {
        val navigationBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val screenAwareLift = when {
            screenHeight < 700.dp -> (-18).dp
            screenHeight > 860.dp -> 18.dp
            else -> 0.dp
        }
        val menuBottomPadding = MenuBaseBottomPadding + navigationBottomPadding + screenAwareLift

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(ScrimColor)
                .caloShapeClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(
                    start = MenuSidePadding,
                    end = MenuSidePadding,
                    bottom = menuBottomPadding
                ),
            horizontalArrangement = Arrangement.spacedBy(MenuCardSpacing)
        ) {
            QuickActionCard(
                label = stringResource(R.string.quick_add_saved_foods),
                testTag = "quick_add_saved_foods_card",
                onClick = onSavedFoodsClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = null,
                        tint = if (HomeCardStyles.isDark()) DarkContentColor else LightContentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            )

            QuickActionCard(
                label = stringResource(R.string.quick_add_scan_food),
                testTag = "quick_add_scan_food_card",
                onClick = onScanFoodClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                icon = {
                    ScanCameraIcon(
                        modifier = Modifier.size(30.dp),
                        frameRatio = 0.86f,
                        cornerLenRatio = 0.30f,
                        cornerRoundness = 0.62f,
                        frameStrokeWidth = 1.5.dp,
                        frameAlpha = 0.62f,
                        plusSizeRatio = 0.44f,
                        plusStrokeWidth = 1.8.dp,
                        color = if (HomeCardStyles.isDark()) DarkContentColor else LightContentColor
                    )
                }
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    label: String,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    val hapticClick = rememberClickWithHaptic(onClick = onClick)
    val isDark = HomeCardStyles.isDark()
    val cardColor = if (isDark) DarkMenuCardColor else CardStyles.bg()
    val contentColor = if (isDark) DarkContentColor else LightContentColor
    val tileColor = if (isDark) DarkTileColor else LightTileColor
    val cardBorder = if (isDark) {
        BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
    } else {
        BorderStroke(1.5.dp, Color(0xFFC6CBD4))
    }

    Card(
        modifier = modifier
            .heightIn(min = 125.dp)
            .testTag(testTag)
            .caloShapeClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = hapticClick
        ),
        shape = RoundedCornerShape(30.dp),
        border = cardBorder,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = tileColor,
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = label,
                color = contentColor,
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}
