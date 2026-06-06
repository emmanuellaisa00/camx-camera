package com.beadrop.camera.ui

import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.beadrop.camera.capture.RecordingState
import com.beadrop.camera.ui.components.*
import com.beadrop.camera.viewmodel.CameraViewModel
import com.beadrop.core.domain.model.*
import com.beadrop.design.components.buttons.CaptureButton
import com.beadrop.design.components.buttons.GlassIconButton
import com.beadrop.design.components.glass.GlassIntensity
import com.beadrop.design.components.glass.GlassPill
import com.beadrop.design.components.glass.GlassSurface
import com.beadrop.design.haptics.rememberHapticEngine
import com.beadrop.design.theme.BeadropTheme
import com.beadrop.design.tokens.ColorTokens
import com.beadrop.design.tokens.SpacingTokens
import com.beadrop.design.tokens.TypographyTokens

/**
 * Main Camera Screen — the flagship experience.
 *
 * Composable hierarchy:
 * - Camera viewfinder (full bleed)
 * - Top controls bar (glassmorphic)
 * - Mode selector strip
 * - Zoom controls
 * - Bottom capture bar
 * - Focus/exposure overlays
 * - Grid overlay
 * - Level indicator
 * - Histogram overlay
 * - Timer countdown overlay
 * - Zoom navigator (at 10x+)
 */
@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel(),
    onNavigateToGallery: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val haptics = rememberHapticEngine()

    // Collect states
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val config by viewModel.config.collectAsStateWithLifecycle()
    val cameraMode by viewModel.cameraMode.collectAsStateWithLifecycle()
    val zoomState by viewModel.zoomState.collectAsStateWithLifecycle()
    val focusState by viewModel.focusState.collectAsStateWithLifecycle()
    val recordingState by viewModel.recordingState.collectAsStateWithLifecycle()
    val recordingDuration by viewModel.recordingDuration.collectAsStateWithLifecycle()
    val isFrontCamera by viewModel.isFrontCamera.collectAsStateWithLifecycle()
    val timerCountdown by viewModel.timerCountdown.collectAsStateWithLifecycle()
    val showNavigator by viewModel.showNavigator.collectAsStateWithLifecycle()
    val showGrid by viewModel.showGrid.collectAsStateWithLifecycle()
    val showLevel by viewModel.showLevel.collectAsStateWithLifecycle()
    val showHistogram by viewModel.showHistogram.collectAsStateWithLifecycle()
    val lastCapturedUri by viewModel.lastCapturedUri.collectAsStateWithLifecycle()

    // Initialize camera
    LaunchedEffect(Unit) {
        viewModel.initializeCamera()
    }

    // Rebind on mode/config/camera changes
    val previewView = remember { mutableStateOf<PreviewView?>(null) }

    LaunchedEffect(cameraMode, config, isFrontCamera) {
        previewView.value?.let { pv ->
            viewModel.getCameraEngine().bindCamera(
                lifecycleOwner = lifecycleOwner,
                surfaceProvider = pv.surfaceProvider,
                mode = cameraMode,
                config = config,
            )
        }
    }

    val isVideoMode = cameraMode == CameraMode.VIDEO ||
            cameraMode == CameraMode.SLOW_MOTION ||
            cameraMode == CameraMode.TIMELAPSE ||
            cameraMode == CameraMode.HYPERLAPSE
    val isRecording = recordingState is RecordingState.Recording || recordingState is RecordingState.Paused

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ═══════════════════════════════════════════
        // LAYER 1: Camera Preview (Full Bleed)
        // ═══════════════════════════════════════════
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                    previewView.value = this
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            haptics.lightTap()
                            viewModel.focusAtPoint(
                                offset.x, offset.y,
                                size.width, size.height,
                            )
                        },
                        onDoubleTap = { offset ->
                            haptics.mediumImpact()
                            viewModel.nextZoomStop()
                        },
                        onLongPress = { offset ->
                            haptics.heavyImpact()
                            viewModel.lockFocus()
                        },
                    )
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        viewModel.onPinchZoom(zoom)
                    }
                },
        )

        // ═══════════════════════════════════════════
        // LAYER 2: Top Gradient Vignette
        // ═══════════════════════════════════════════
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ColorTokens.ViewfinderGradientTop,
                            ColorTokens.ViewfinderClear,
                        )
                    )
                )
        )

        // ═══════════════════════════════════════════
        // LAYER 3: Bottom Gradient Vignette
        // ═══════════════════════════════════════════
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ColorTokens.ViewfinderClear,
                            ColorTokens.ViewfinderGradientBottom,
                        )
                    )
                )
        )

        // ═══════════════════════════════════════════
        // LAYER 4: Grid Overlay
        // ═══════════════════════════════════════════
        AnimatedVisibility(
            visible = showGrid,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            GridOverlay(gridType = config.gridType)
        }

        // ═══════════════════════════════════════════
        // LAYER 5: Focus Ring
        // ═══════════════════════════════════════════
        AnimatedVisibility(
            visible = focusState.focusX > 0f || focusState.focusY > 0f,
            enter = scaleIn(initialScale = 1.5f) + fadeIn(),
            exit = scaleOut(targetScale = 0.8f) + fadeOut(),
        ) {
            FocusRingOverlay(
                focusX = focusState.focusX,
                focusY = focusState.focusY,
                isFocused = focusState.isFocused,
                isLocked = focusState.isLocked,
            )
        }

        // ═══════════════════════════════════════════
        // LAYER 6: Timer Countdown
        // ═══════════════════════════════════════════
        timerCountdown?.let { seconds ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = seconds.toString(),
                    style = TypographyTokens.CameraTimer,
                    color = Color.White,
                )
            }
        }

        // ═══════════════════════════════════════════
        // LAYER 7: Top Controls Bar
        // ═══════════════════════════════════════════
        CameraTopBar(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = SpacingTokens.L, vertical = SpacingTokens.S),
            config = config,
            cameraMode = cameraMode,
            isRecording = isRecording,
            onFlashClick = { 
                haptics.lightTap()
                viewModel.cycleFlashMode() 
            },
            onTimerClick = {
                haptics.lightTap()
                val timers = TimerDuration.entries
                val idx = timers.indexOf(config.timer)
                viewModel.setTimer(timers[(idx + 1) % timers.size])
            },
            onAspectRatioClick = {
                haptics.lightTap()
                val ratios = AspectRatio.entries
                val idx = ratios.indexOf(config.aspectRatio)
                viewModel.setAspectRatio(ratios[(idx + 1) % ratios.size])
            },
            onHDRClick = {
                haptics.lightTap()
                viewModel.toggleHDR()
            },
            onSettingsClick = onNavigateToSettings,
        )

        // ═══════════════════════════════════════════
        // LAYER 8: Recording Duration
        // ═══════════════════════════════════════════
        AnimatedVisibility(
            visible = isRecording,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 64.dp),
        ) {
            RecordingIndicator(duration = recordingDuration)
        }

        // ═══════════════════════════════════════════
        // LAYER 9: Zoom Navigator (10x+)
        // ═══════════════════════════════════════════
        AnimatedVisibility(
            visible = showNavigator,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 80.dp, end = SpacingTokens.L),
        ) {
            ZoomNavigatorWindow()
        }

        // ═══════════════════════════════════════════
        // LAYER 10: Histogram
        // ═══════════════════════════════════════════
        AnimatedVisibility(
            visible = showHistogram,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 80.dp, start = SpacingTokens.L),
        ) {
            HistogramOverlay()
        }

        // ═══════════════════════════════════════════
        // LAYER 11: Zoom Control Strip
        // ═══════════════════════════════════════════
        ZoomControlStrip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 200.dp),
            currentZoom = zoomState.zoomRatio,
            onZoomSelected = { ratio ->
                haptics.zoomStop()
                viewModel.setZoomRatio(ratio)
            },
        )

        // ═══════════════════════════════════════════
        // LAYER 12: Mode Selector
        // ═══════════════════════════════════════════
        CameraModeSelectorStrip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 150.dp),
            currentMode = cameraMode,
            onModeSelected = { mode ->
                haptics.mediumImpact()
                viewModel.setMode(mode)
            },
        )

        // ═══════════════════════════════════════════
        // LAYER 13: Bottom Capture Bar
        // ═══════════════════════════════════════════
        CameraBottomBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = SpacingTokens.XXL),
            isVideoMode = isVideoMode,
            isRecording = isRecording,
            lastCapturedUri = lastCapturedUri,
            onCapture = {
                haptics.heavyImpact()
                if (isVideoMode) {
                    if (isRecording) {
                        viewModel.stopVideoRecording()
                    } else {
                        viewModel.startVideoRecording()
                    }
                } else {
                    viewModel.capturePhoto()
                }
            },
            onSwitchCamera = {
                haptics.mediumImpact()
                viewModel.switchCamera()
            },
            onGalleryClick = {
                haptics.lightTap()
                onNavigateToGallery()
            },
        )

        // ═══════════════════════════════════════════
        // LAYER 14: Level Indicator
        // ═══════════════════════════════════════════
        AnimatedVisibility(
            visible = showLevel,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center),
        ) {
            LevelIndicatorOverlay()
        }
    }
}
