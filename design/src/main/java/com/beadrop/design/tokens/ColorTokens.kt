package com.beadrop.design.tokens

import androidx.compose.ui.graphics.Color

/**
 * Beadrop Camera — Clean Samsung One UI Dark Color System
 * 
 * Minimal, dark, precise. No glassmorphism. Clean floating elements.
 */
object ColorTokens {

    // ═══════════════════════════════════════════════
    // PRIMARY — Samsung Blue
    // ═══════════════════════════════════════════════
    val Primary = Color(0xFF4A9DFF)
    val PrimaryDark = Color(0xFF2979FF)
    val OnPrimary = Color(0xFFFFFFFF)

    // ═══════════════════════════════════════════════
    // SURFACE — Pure black camera background
    // ═══════════════════════════════════════════════
    val SurfacePure = Color(0xFF000000)
    val SurfaceDim = Color(0xFF0D0D0D)
    val SurfaceCard = Color(0xFF1A1A1A)
    val SurfaceElevated = Color(0xFF252525)
    val SurfaceControl = Color(0xFF2C2C2C)

    // ═══════════════════════════════════════════════
    // TEXT
    // ═══════════════════════════════════════════════
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFB3B3B3)
    val TextTertiary = Color(0xFF737373)
    val TextDisabled = Color(0xFF404040)

    // ═══════════════════════════════════════════════
    // CAMERA-SPECIFIC
    // ═══════════════════════════════════════════════
    val CaptureButton = Color(0xFFFFFFFF)
    val RecordingRed = Color(0xFFFF3B30)
    val RecordingPulse = Color(0x80FF3B30)
    val FocusRing = Color(0xFFFFCC00)
    val FocusLocked = Color(0xFF34C759)
    val GridLine = Color(0x40FFFFFF)
    val LevelIndicator = Color(0xFFFFCC00)
    val LevelAligned = Color(0xFF34C759)

    // Zoom dial
    val ZoomDialBg = Color(0xCC000000)         // 80% black
    val ZoomDialActive = Color(0xFFFFCC00)     // Samsung gold
    val ZoomDialInactive = Color(0xFF808080)
    val ZoomDialTrack = Color(0xFF333333)

    // Mode selector
    val ModeActive = Color(0xFFFFCC00)
    val ModeInactive = Color(0xFF999999)

    // Top bar controls
    val TopBarBg = Color(0x00000000)            // Transparent
    val TopBarIcon = Color(0xFFFFFFFF)
    val TopBarIconActive = Color(0xFFFFCC00)

    // Viewfinder gradients
    val ViewfinderGradientTop = Color(0x66000000)
    val ViewfinderGradientBottom = Color(0x99000000)
    val ViewfinderClear = Color(0x00000000)

    // Semantic
    val Success = Color(0xFF34C759)
    val Warning = Color(0xFFFFCC00)
    val Error = Color(0xFFFF3B30)
    val Accent = Color(0xFFFFCC00)

    // ═══════════════════════════════════════════════
    // GLASS SYSTEM (kept for gallery/settings compat)
    // ═══════════════════════════════════════════════
    val GlassUltraThin = Color(0x0DFFFFFF)
    val GlassThin = Color(0x1AFFFFFF)
    val GlassRegular = Color(0x33FFFFFF)
    val GlassThick = Color(0x4DFFFFFF)
    val GlassUltraThick = Color(0x66FFFFFF)
    val GlassBorderLight = Color(0x26FFFFFF)
    val GlassBorderMedium = Color(0x33FFFFFF)
    val GlassBorderStrong = Color(0x4DFFFFFF)
    val GlassShadow = Color(0x40000000)
    val GlassHighlight = Color(0x1AFFFFFF)

    // ═══════════════════════════════════════════════
    // THEME COMPAT
    // ═══════════════════════════════════════════════
    val PrimaryContainer = Color(0xFF1A1A2E)
    val OnPrimaryContainer = Color(0xFFD6E4FF)
    val Secondary = Color(0xFF8B5CF6)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFF1E1E2E)
    val OnSecondaryContainer = Color(0xFFEDE9FE)
    val ErrorContainer = Color(0xFF7F1D1D)
    val SurfaceDeep = Color(0xFF0A0A0A)
    val HistogramFill = Color(0x80FFFFFF)
    val FocusPeaking = Color(0xFF00FF00)
    val TextLink = Color(0xFF60A5FA)
    val TextInverse = Color(0xFF0F172A)
    val OnPrimary_compat = Color(0xFFFFFFFF)
}
