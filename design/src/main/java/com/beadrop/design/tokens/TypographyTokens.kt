package com.beadrop.design.tokens

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

/**
 * Beadrop Camera Typography System
 * 
 * Clean, precise, premium typographic hierarchy.
 * Uses system SF-style font on all devices for maximum clarity.
 */
object TypographyTokens {

    private val cameraFontFamily = FontFamily.Default

    // ═══════════════════════════════════════════════
    // DISPLAY — Hero text, splash, branding
    // ═══════════════════════════════════════════════
    val DisplayLarge = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    )

    val DisplayMedium = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    )

    val DisplaySmall = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    )

    // ═══════════════════════════════════════════════
    // HEADLINE — Section headers, titles
    // ═══════════════════════════════════════════════
    val HeadlineLarge = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    )

    val HeadlineMedium = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    )

    val HeadlineSmall = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    )

    // ═══════════════════════════════════════════════
    // TITLE — Card titles, list headers
    // ═══════════════════════════════════════════════
    val TitleLarge = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    )

    val TitleMedium = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    )

    val TitleSmall = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    )

    // ═══════════════════════════════════════════════
    // BODY — Content text
    // ═══════════════════════════════════════════════
    val BodyLarge = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    )

    val BodyMedium = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    )

    val BodySmall = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    )

    // ═══════════════════════════════════════════════
    // LABEL — Buttons, tags, chips
    // ═══════════════════════════════════════════════
    val LabelLarge = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    )

    val LabelMedium = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )

    val LabelSmall = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )

    // ═══════════════════════════════════════════════
    // CAMERA-SPECIFIC TYPOGRAPHY
    // ═══════════════════════════════════════════════
    val CameraMode = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.sp,
    )

    val CameraZoom = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp,
    )

    val CameraInfo = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.2.sp,
    )

    val CameraTimer = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 72.sp,
        lineHeight = 80.sp,
        letterSpacing = (-1).sp,
    )

    val ProModeValue = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )

    val MetadataKey = TextStyle(
        fontFamily = cameraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    )

    val MetadataValue = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp,
    )
}
