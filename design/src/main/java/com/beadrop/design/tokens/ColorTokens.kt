package com.beadrop.design.tokens

import androidx.compose.ui.graphics.Color

/**
 * Beadrop Camera Design Tokens — Color System
 * 
 * Inspired by VisionOS glass depth, Samsung camera precision, and Apple fluidity.
 * Every color is crafted for premium feel in both light and dark contexts.
 */
object ColorTokens {

    // ═══════════════════════════════════════════════
    // PRIMARY PALETTE — Deep Space Blue
    // ═══════════════════════════════════════════════
    val Primary = Color(0xFF3B82F6)
    val PrimaryLight = Color(0xFF60A5FA)
    val PrimaryDark = Color(0xFF2563EB)
    val PrimaryContainer = Color(0xFF1E3A5F)
    val OnPrimary = Color(0xFFFFFFFF)
    val OnPrimaryContainer = Color(0xFFD6E4FF)

    // ═══════════════════════════════════════════════
    // SECONDARY PALETTE — Luminous Violet
    // ═══════════════════════════════════════════════
    val Secondary = Color(0xFF8B5CF6)
    val SecondaryLight = Color(0xFFA78BFA)
    val SecondaryDark = Color(0xFF7C3AED)
    val SecondaryContainer = Color(0xFF2E1065)
    val OnSecondary = Color(0xFFFFFFFF)
    val OnSecondaryContainer = Color(0xFFEDE9FE)

    // ═══════════════════════════════════════════════
    // ACCENT — Camera Gold
    // ═══════════════════════════════════════════════
    val Accent = Color(0xFFF59E0B)
    val AccentLight = Color(0xFFFBBF24)
    val AccentDark = Color(0xFFD97706)

    // ═══════════════════════════════════════════════
    // SURFACE SYSTEM
    // ═══════════════════════════════════════════════
    val SurfacePure = Color(0xFF000000)
    val SurfaceDeep = Color(0xFF0A0A0A)
    val SurfaceDark = Color(0xFF121212)
    val SurfaceElevated = Color(0xFF1A1A1A)
    val SurfaceCard = Color(0xFF1E1E1E)
    val SurfaceSheet = Color(0xFF242424)
    val SurfaceOverlay = Color(0xFF2A2A2A)

    // ═══════════════════════════════════════════════
    // GLASS SYSTEM — VisionOS Inspired
    // ═══════════════════════════════════════════════
    val GlassUltraThin = Color(0x0DFFFFFF)       // 5% white
    val GlassThin = Color(0x1AFFFFFF)             // 10% white
    val GlassRegular = Color(0x33FFFFFF)          // 20% white
    val GlassThick = Color(0x4DFFFFFF)            // 30% white
    val GlassUltraThick = Color(0x66FFFFFF)       // 40% white

    val GlassBorderLight = Color(0x26FFFFFF)      // 15% white
    val GlassBorderMedium = Color(0x33FFFFFF)     // 20% white
    val GlassBorderStrong = Color(0x4DFFFFFF)     // 30% white

    val GlassShadow = Color(0x40000000)           // 25% black
    val GlassHighlight = Color(0x1AFFFFFF)        // 10% white
    val GlassReflection = Color(0x0DFFFFFF)       // 5% white

    // Dark glass variants
    val GlassDarkThin = Color(0x1A000000)         // 10% black
    val GlassDarkRegular = Color(0x33000000)      // 20% black
    val GlassDarkThick = Color(0x4D000000)        // 30% black

    // ═══════════════════════════════════════════════
    // TEXT SYSTEM
    // ═══════════════════════════════════════════════
    val TextPrimary = Color(0xFFF8FAFC)
    val TextSecondary = Color(0xFFCBD5E1)
    val TextTertiary = Color(0xFF94A3B8)
    val TextDisabled = Color(0xFF475569)
    val TextInverse = Color(0xFF0F172A)
    val TextLink = Color(0xFF60A5FA)

    // ═══════════════════════════════════════════════
    // SEMANTIC COLORS
    // ═══════════════════════════════════════════════
    val Success = Color(0xFF10B981)
    val SuccessLight = Color(0xFF34D399)
    val SuccessContainer = Color(0xFF064E3B)
    
    val Warning = Color(0xFFF59E0B)
    val WarningLight = Color(0xFFFBBF24)
    val WarningContainer = Color(0xFF78350F)
    
    val Error = Color(0xFFEF4444)
    val ErrorLight = Color(0xFFF87171)
    val ErrorContainer = Color(0xFF7F1D1D)
    
    val Info = Color(0xFF06B6D4)
    val InfoLight = Color(0xFF22D3EE)
    val InfoContainer = Color(0xFF164E63)

    // ═══════════════════════════════════════════════
    // CAMERA-SPECIFIC COLORS
    // ═══════════════════════════════════════════════
    val CaptureButton = Color(0xFFFFFFFF)
    val CaptureButtonActive = Color(0xFFEF4444)
    val RecordingRed = Color(0xFFEF4444)
    val RecordingPulse = Color(0x80EF4444)
    val FocusRing = Color(0xFFFBBF24)
    val FocusLocked = Color(0xFF10B981)
    val GridLine = Color(0x4DFFFFFF)
    val LevelIndicator = Color(0xFFFBBF24)
    val LevelAligned = Color(0xFF10B981)
    val HistogramFill = Color(0x80FFFFFF)
    val ZebraStripe = Color(0x80FF0000)
    val FocusPeaking = Color(0xFF00FF00)

    // ═══════════════════════════════════════════════
    // GRADIENT COMPONENTS
    // ═══════════════════════════════════════════════
    val GradientStart = Color(0xFF3B82F6)
    val GradientMid = Color(0xFF8B5CF6)
    val GradientEnd = Color(0xFFEC4899)

    val ViewfinderGradientTop = Color(0x80000000)
    val ViewfinderGradientBottom = Color(0xCC000000)
    val ViewfinderClear = Color(0x00000000)
}
