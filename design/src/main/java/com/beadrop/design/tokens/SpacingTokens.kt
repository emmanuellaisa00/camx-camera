package com.beadrop.design.tokens

import androidx.compose.ui.unit.dp

/**
 * Beadrop Camera Spacing System
 * 
 * Consistent spatial rhythm using 4dp base grid.
 */
object SpacingTokens {
    val None = 0.dp
    val XXS = 2.dp
    val XS = 4.dp
    val S = 8.dp
    val M = 12.dp
    val L = 16.dp
    val XL = 20.dp
    val XXL = 24.dp
    val XXXL = 32.dp
    val Huge = 40.dp
    val Massive = 48.dp
    val Giant = 56.dp
    val Colossal = 64.dp
    val Epic = 80.dp
    val Ultra = 96.dp
}

object RadiusTokens {
    val None = 0.dp
    val XS = 4.dp
    val S = 8.dp
    val M = 12.dp
    val L = 16.dp
    val XL = 20.dp
    val XXL = 24.dp
    val XXXL = 28.dp
    val Round = 32.dp
    val Pill = 100.dp
    val Full = 999.dp
}

object ElevationTokens {
    val None = 0.dp
    val XS = 1.dp
    val S = 2.dp
    val M = 4.dp
    val L = 8.dp
    val XL = 12.dp
    val XXL = 16.dp
    val XXXL = 24.dp
}

object SizeTokens {
    // Icon sizes
    val IconXS = 16.dp
    val IconS = 20.dp
    val IconM = 24.dp
    val IconL = 28.dp
    val IconXL = 32.dp
    val IconXXL = 40.dp
    val IconHuge = 48.dp

    // Button sizes
    val ButtonHeightS = 32.dp
    val ButtonHeightM = 40.dp
    val ButtonHeightL = 48.dp
    val ButtonHeightXL = 56.dp

    // Camera control sizes
    val CaptureButtonOuter = 80.dp
    val CaptureButtonInner = 64.dp
    val CaptureButtonRing = 4.dp
    val ZoomChipHeight = 32.dp
    val ZoomChipWidth = 44.dp
    val ModeChipHeight = 28.dp
    val ControlBarHeight = 120.dp
    val TopBarHeight = 56.dp
    val BottomSheetHandle = 4.dp
    val BottomSheetHandleWidth = 36.dp
    
    // Gallery
    val ThumbnailSmall = 80.dp
    val ThumbnailMedium = 120.dp
    val ThumbnailLarge = 180.dp
    
    // Minimum touch target
    val MinTouchTarget = 48.dp
}

object DurationTokens {
    const val Instant = 100L
    const val Fast = 200L
    const val Normal = 300L
    const val Slow = 500L
    const val Glacial = 800L
    const val ExtraSlow = 1000L
}
