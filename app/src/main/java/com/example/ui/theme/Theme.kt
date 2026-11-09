package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFFFF5252),
  onPrimary = Color.White,
  primaryContainer = EmergencyRedDark,
  onPrimaryContainer = Color.White,
  secondary = Color(0xFF40C4FF),
  onSecondary = Color.Black,
  secondaryContainer = SignalBlueDark,
  onSecondaryContainer = Color.White,
  tertiary = WarningAmber,
  background = DarkBackground,
  onBackground = DarkTextPrimary,
  surface = DarkSurface,
  onSurface = DarkTextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
  primary = EmergencyRed,
  onPrimary = Color.White,
  primaryContainer = EmergencyRedLight,
  onPrimaryContainer = EmergencyRedDark,
  secondary = SignalBlue,
  onSecondary = Color.White,
  secondaryContainer = SignalBlueLight,
  onSecondaryContainer = SignalBlueDark,
  tertiary = WarningAmber,
  background = LightBackground,
  onBackground = LightTextPrimary,
  surface = LightSurface,
  onSurface = LightTextPrimary,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = LightTextSecondary
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep high-contrast emergency palette predictable
  content: @Composable () -> Unit,
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
