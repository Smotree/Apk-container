package com.apkcontainer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Blue90,
    onPrimaryContainer = Blue10,
    secondary = Green40,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Green90,
    onSecondaryContainer = Green10,
    tertiary = Purple40,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = Purple90,
    onTertiaryContainer = Purple10,
    error = ErrorRed,
    errorContainer = ErrorRedLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceVariantLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue20,
    primaryContainer = Blue30,
    onPrimaryContainer = Blue90,
    secondary = Green80,
    onSecondary = Green20,
    secondaryContainer = Green30,
    onSecondaryContainer = Green90,
    tertiary = Purple80,
    onTertiary = Purple20,
    tertiaryContainer = Purple30,
    onTertiaryContainer = Purple90,
    error = ErrorRedLight,
    errorContainer = ErrorRed,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

@Composable
fun ApkContainerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
