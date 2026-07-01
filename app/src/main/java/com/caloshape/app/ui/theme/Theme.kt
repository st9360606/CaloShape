package com.caloshape.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.caloshape.app.ui.common.design.CaloShapeColors

private val DarkColorScheme = darkColorScheme(
    primary = CaloShapeColors.Dark.primaryButtonContainer,
    onPrimary = CaloShapeColors.Dark.primaryButtonContent,
    secondary = CaloShapeColors.Dark.textSecondary,
    onSecondary = CaloShapeColors.Dark.textPrimary,
    background = CaloShapeColors.Dark.background,
    onBackground = CaloShapeColors.Dark.textPrimary,
    surface = CaloShapeColors.Dark.surface,
    onSurface = CaloShapeColors.Dark.textPrimary,
    surfaceVariant = CaloShapeColors.Dark.surfaceMuted,
    onSurfaceVariant = CaloShapeColors.Dark.textSecondary,
    outline = CaloShapeColors.Dark.border,
    error = CaloShapeColors.Dark.error,
)

private val LightColorScheme = lightColorScheme(
    primary = CaloShapeColors.Light.primaryButtonContainer,
    onPrimary = CaloShapeColors.Light.primaryButtonContent,
    secondary = CaloShapeColors.Light.textSecondary,
    onSecondary = CaloShapeColors.Light.textPrimary,
    background = CaloShapeColors.Light.background,
    onBackground = CaloShapeColors.Light.textPrimary,
    surface = CaloShapeColors.Light.surface,
    onSurface = CaloShapeColors.Light.textPrimary,
    surfaceVariant = CaloShapeColors.Light.surfaceMuted,
    onSurfaceVariant = CaloShapeColors.Light.textSecondary,
    outline = CaloShapeColors.Light.border,
    error = CaloShapeColors.Light.error,
)

@Composable
fun CaloShapeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
