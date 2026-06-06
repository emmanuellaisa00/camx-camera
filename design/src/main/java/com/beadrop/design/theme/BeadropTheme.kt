package com.beadrop.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.beadrop.design.tokens.ColorTokens
import com.beadrop.design.tokens.TypographyTokens

/**
 * Beadrop Camera Theme
 * 
 * Premium dark-first camera theme with glassmorphism foundation.
 * Camera apps are always dark — we optimize for that.
 */

// Material 3 dark color scheme tuned for camera
private val BeadropDarkColorScheme = darkColorScheme(
    primary = ColorTokens.Primary,
    onPrimary = ColorTokens.OnPrimary,
    primaryContainer = ColorTokens.PrimaryContainer,
    onPrimaryContainer = ColorTokens.OnPrimaryContainer,
    secondary = ColorTokens.Secondary,
    onSecondary = ColorTokens.OnSecondary,
    secondaryContainer = ColorTokens.SecondaryContainer,
    onSecondaryContainer = ColorTokens.OnSecondaryContainer,
    tertiary = ColorTokens.Accent,
    error = ColorTokens.Error,
    onError = ColorTokens.OnPrimary,
    errorContainer = ColorTokens.ErrorContainer,
    background = ColorTokens.SurfacePure,
    onBackground = ColorTokens.TextPrimary,
    surface = ColorTokens.SurfaceDeep,
    onSurface = ColorTokens.TextPrimary,
    surfaceVariant = ColorTokens.SurfaceElevated,
    onSurfaceVariant = ColorTokens.TextSecondary,
    outline = ColorTokens.GlassBorderMedium,
    outlineVariant = ColorTokens.GlassBorderLight,
    inverseSurface = ColorTokens.TextPrimary,
    inverseOnSurface = ColorTokens.SurfacePure,
)

// Extended color properties for camera-specific use
data class BeadropExtendedColors(
    val glassThin: androidx.compose.ui.graphics.Color = ColorTokens.GlassThin,
    val glassRegular: androidx.compose.ui.graphics.Color = ColorTokens.GlassRegular,
    val glassThick: androidx.compose.ui.graphics.Color = ColorTokens.GlassThick,
    val glassBorder: androidx.compose.ui.graphics.Color = ColorTokens.GlassBorderMedium,
    val glassShadow: androidx.compose.ui.graphics.Color = ColorTokens.GlassShadow,
    val glassHighlight: androidx.compose.ui.graphics.Color = ColorTokens.GlassHighlight,
    val captureButton: androidx.compose.ui.graphics.Color = ColorTokens.CaptureButton,
    val captureActive: androidx.compose.ui.graphics.Color = ColorTokens.RecordingRed,
    val recording: androidx.compose.ui.graphics.Color = ColorTokens.RecordingRed,
    val focusRing: androidx.compose.ui.graphics.Color = ColorTokens.FocusRing,
    val focusLocked: androidx.compose.ui.graphics.Color = ColorTokens.FocusLocked,
    val gridLine: androidx.compose.ui.graphics.Color = ColorTokens.GridLine,
    val levelIndicator: androidx.compose.ui.graphics.Color = ColorTokens.LevelIndicator,
    val levelAligned: androidx.compose.ui.graphics.Color = ColorTokens.LevelAligned,
    val textPrimary: androidx.compose.ui.graphics.Color = ColorTokens.TextPrimary,
    val textSecondary: androidx.compose.ui.graphics.Color = ColorTokens.TextSecondary,
    val textTertiary: androidx.compose.ui.graphics.Color = ColorTokens.TextTertiary,
    val success: androidx.compose.ui.graphics.Color = ColorTokens.Success,
    val warning: androidx.compose.ui.graphics.Color = ColorTokens.Warning,
    val accent: androidx.compose.ui.graphics.Color = ColorTokens.Accent,
)

val LocalBeadropColors = staticCompositionLocalOf { BeadropExtendedColors() }

// Material 3 Typography mapping
private val BeadropTypography = Typography(
    displayLarge = TypographyTokens.DisplayLarge,
    displayMedium = TypographyTokens.DisplayMedium,
    displaySmall = TypographyTokens.DisplaySmall,
    headlineLarge = TypographyTokens.HeadlineLarge,
    headlineMedium = TypographyTokens.HeadlineMedium,
    headlineSmall = TypographyTokens.HeadlineSmall,
    titleLarge = TypographyTokens.TitleLarge,
    titleMedium = TypographyTokens.TitleMedium,
    titleSmall = TypographyTokens.TitleSmall,
    bodyLarge = TypographyTokens.BodyLarge,
    bodyMedium = TypographyTokens.BodyMedium,
    bodySmall = TypographyTokens.BodySmall,
    labelLarge = TypographyTokens.LabelLarge,
    labelMedium = TypographyTokens.LabelMedium,
    labelSmall = TypographyTokens.LabelSmall,
)

@Composable
fun BeadropTheme(
    content: @Composable () -> Unit,
) {
    val extendedColors = BeadropExtendedColors()

    CompositionLocalProvider(
        LocalBeadropColors provides extendedColors,
    ) {
        MaterialTheme(
            colorScheme = BeadropDarkColorScheme,
            typography = BeadropTypography,
            content = content,
        )
    }
}

/**
 * Access extended Beadrop colors from any composable.
 */
object BeadropTheme {
    val colors: BeadropExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalBeadropColors.current
}
