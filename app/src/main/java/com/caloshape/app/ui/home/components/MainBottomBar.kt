package com.caloshape.app.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.caloshape.app.R
import com.caloshape.app.ui.common.haptic.rememberClickWithHaptic
import com.caloshape.app.ui.common.design.CaloShapeColors
import com.caloshape.app.ui.home.HomeTab

/**
 * 共用 BottomBar：Home / Progress / Weight / Fasting / Workout
 */
@Composable
fun MainBottomBar(
    current: HomeTab,
    onOpenTab: (HomeTab) -> Unit
) {
    val colors = CaloShapeColors.current()
    val barSurface = colors.background
    val selected = colors.textPrimary
    val unselected = colors.textMuted

    Column(
        modifier = Modifier
            .background(barSurface)
            .navigationBarsPadding()
    ) {
        NavigationBar(
            modifier = Modifier.padding(horizontal = 8.dp),
            containerColor = barSurface,
            tonalElevation = 0.dp
        ) {
            @Composable
            fun itemColors() = NavigationBarItemDefaults.colors(
                selectedIconColor = selected,
                selectedTextColor = selected,
                unselectedIconColor = unselected,
                unselectedTextColor = unselected,
                indicatorColor = Color.Transparent
            )

            @Composable
            fun NavItem(
                tab: HomeTab,
                label: String,
                icon: @Composable (tint: Color) -> Unit
            ) {
                val isSelected = current == tab
                val tint = if (isSelected) selected else unselected
                val hapticClick = rememberClickWithHaptic { onOpenTab(tab) }

                NavigationBarItem(
                    selected = isSelected,
                    onClick = hapticClick,
                    alwaysShowLabel = true,
                    label = { Text(text = label, color = tint) },     // ✅ 強制 label 顏色
                    icon = { icon(tint) },                            // ✅ 強制 icon tint
                    colors = itemColors()
                )
            }

            NavItem(tab = HomeTab.Home, label = stringResource(R.string.bottom_nav_home)) { tint ->
                Icon(Icons.Filled.Home, contentDescription = null, tint = tint)
            }
            NavItem(tab = HomeTab.Progress, label = stringResource(R.string.bottom_nav_progress)) { tint ->
                Icon(Icons.Filled.BarChart, contentDescription = null, tint = tint)
            }
            NavItem(tab = HomeTab.Weight, label = stringResource(R.string.bottom_nav_weight)) { tint ->
                Icon(Icons.Filled.MonitorWeight, contentDescription = null, tint = tint)
            }
            NavItem(tab = HomeTab.Fasting, label = stringResource(R.string.bottom_nav_fasting)) { tint ->
                Icon(Icons.Filled.AccessTime, contentDescription = null, tint = tint)
            }
            NavItem(tab = HomeTab.Workout, label = stringResource(R.string.bottom_nav_workout)) { tint ->
                Icon(Icons.Filled.FitnessCenter, contentDescription = null, tint = tint)
            }
        }
    }
}
