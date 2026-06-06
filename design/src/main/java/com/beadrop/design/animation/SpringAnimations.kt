package com.beadrop.design.animation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.beadrop.design.tokens.DurationTokens

/**
 * Beadrop Camera Spring Animation System
 * 
 * Every animation uses spring physics for natural, premium feel.
 * Tuned for flagship-quality motion language.
 */
object BeadropSprings {

    // ═══════════════════════════════════════════════
    // CORE SPRING SPECS
    // ═══════════════════════════════════════════════
    
    /** Quick, responsive spring — for button presses, toggles */
    val Quick = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessHigh,
    )

    /** Standard spring — for most UI transitions */
    val Standard = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium,
    )

    /** Smooth spring — for slow, graceful transitions */
    val Smooth = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow,
    )

    /** Bouncy spring — for playful, satisfying interactions */
    val Bouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    )

    /** Gentle spring — for subtle animations */
    val Gentle = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow,
    )

    /** Snappy spring — for mode switching, lens transitions */
    val Snappy = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessHigh,
    )

    /** Camera spring — tuned for viewfinder interactions */
    val Camera = spring<Float>(
        dampingRatio = 0.75f,
        stiffness = 400f,
    )

    /** Focus spring — for focus ring animation */
    val Focus = spring<Float>(
        dampingRatio = 0.65f,
        stiffness = 500f,
    )

    /** Zoom spring — for smooth zoom transitions */
    val Zoom = spring<Float>(
        dampingRatio = 0.8f,
        stiffness = 300f,
    )

    /** Sheet spring — for bottom sheet expansion */
    val Sheet = spring<Float>(
        dampingRatio = 0.85f,
        stiffness = 350f,
    )

    /** Gallery spring — for gallery item transitions */
    val Gallery = spring<Float>(
        dampingRatio = 0.9f,
        stiffness = 250f,
    )
}

/**
 * Reusable enter/exit transitions for premium feel.
 */
object BeadropTransitions {

    // Fade
    val fadeIn: EnterTransition = fadeIn(
        animationSpec = tween(DurationTokens.Normal.toInt())
    )
    val fadeOut: ExitTransition = fadeOut(
        animationSpec = tween(DurationTokens.Fast.toInt())
    )

    // Scale + Fade (for capturing, notifications)
    val scaleIn: EnterTransition = scaleIn(
        initialScale = 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        )
    ) + fadeIn(animationSpec = tween(DurationTokens.Fast.toInt()))

    val scaleOut: ExitTransition = scaleOut(
        targetScale = 0.8f,
        animationSpec = tween(DurationTokens.Fast.toInt())
    ) + fadeOut(animationSpec = tween(DurationTokens.Fast.toInt()))

    // Slide from bottom (for sheets)
    val slideUpEnter: EnterTransition = slideInVertically(
        initialOffsetY = { it },
        animationSpec = spring(
            dampingRatio = 0.85f,
            stiffness = 350f,
        )
    ) + fadeIn(animationSpec = tween(DurationTokens.Normal.toInt()))

    val slideDownExit: ExitTransition = slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(DurationTokens.Normal.toInt())
    ) + fadeOut(animationSpec = tween(DurationTokens.Fast.toInt()))

    // Slide horizontal (for gallery navigation)
    fun slideInFromRight(): EnterTransition = slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        )
    ) + fadeIn(animationSpec = tween(DurationTokens.Fast.toInt()))

    fun slideOutToLeft(): ExitTransition = slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(DurationTokens.Normal.toInt())
    ) + fadeOut(animationSpec = tween(DurationTokens.Fast.toInt()))

    fun slideInFromLeft(): EnterTransition = slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        )
    ) + fadeIn(animationSpec = tween(DurationTokens.Fast.toInt()))

    fun slideOutToRight(): ExitTransition = slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(DurationTokens.Normal.toInt())
    ) + fadeOut(animationSpec = tween(DurationTokens.Fast.toInt()))
}
