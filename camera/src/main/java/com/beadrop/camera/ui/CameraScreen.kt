package com.beadrop.camera.ui

import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.beadrop.camera.capture.RecordingState
import com.beadrop.camera.ui.components.*
import com.beadrop.camera.viewmodel.CameraViewModel
import com.beadrop.core.domain.model.*
import com.beadrop.design.haptics.rememberHapticEngine
import com.beadrop.design.tokens.ColorTokens

/**
 * Main Camera Screen — Clean Samsung One UI Dark Style.
 *
 * Layout (top to bottom):
 * 1. Top controls (flash, ratio, timer, settings) — minimal white icons
 * 2. Full-bleed viewfinder with focus/grid overlays
 * 3. Zoom quick-pills (0.5x, 1x, 2x, 5x, 10x)
 * 4. Mode selector strip (NIGHT, PORTRAIT, PHOTO, VIDEO)
 * 5. Bottom bar: gallery thumbnail | capture button | switch camera
 */
@Composable
fun CameraScreen(
    viewModel: CameraViewModel = hiltViewModel(),
    onNavigateToGallery: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val haptics = rememberHapticEngine()

    // Collect states
    val cameraMode by viewModel.cameraMode.collectAsStateWithLifecycle()
    val config by viewModel.config.collectAsStateWithLifecycle()
    val zoomState by viewModel.zoomState.collectAsStateWithLifecycle()
    val focusState by viewModel.focusState.collectAsStateWithLifecycle()
    val recordingState by viewModel.recordingState.collectAsStateWithLifecycle()
    val recordingDuration by viewModel.recordingDuration.collectAsStateWithLifecycle()
    val isFrontCamera by viewModel.isFrontCamera.collectAsStateWithLifecycle()
    val timerCountdown by viewModel.timerCountdown.collectAsStateWithLifecycle()
    val showGrid by viewModel.showGrid.collectAsStateWithLifecycle()
    val lastCapturedUri by viewModel.lastCapturedUri.collectAsStateWithLifecycle()

    // Capture flash animation
    var showCaptureFlash by remember { mutableStateOf(false) }
    var captureScale by remember { mutableFloatStateOf(1f) }

    val flashAlpha by animateFloatAsState(
        targetValue = if (showCaptureFlash) 1f else 0f,
        animationSpec = tween(80),
        label = "flashAlpha",
        finishedListener = { showCaptureFlash = false },
    )

    val captureButtonScale by animateFloatAsState(
        targetValue = captureScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh,
        ),
        label = "captureBounce",
    )

    // Initialize camera
    LaunchedEffect(Unit) {
        viewModel.initializeCamera()
    }

    val previewView = remember { mutableStateOf<PreviewView?>(null) }

    // Rebind on mode/config changes
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

    val isVideoMode = cameraMode == CameraMode.VIDEO
    val isRecording = recordingState is RecordingState.Recording || recordingState is RecordingState.Paused

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ═══════════════════════════════════════════
        // VIEWFINDER
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
                        onDoubleTap = {
                            haptics.mediumImpact()
                            viewModel.nextZoomStop()
                        },
                        onLongPress = {
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
        // GRID OVERLAY
        // ═══════════════════════════════════════════
        AnimatedVisibility(
            visible = showGrid,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            GridOverlay(gridType = config.gridType)
        }

        // ═══════════════════════════════════════════
        // FOCUS RING
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
        // CAPTURE FLASH (iOS-style white flash)
        // ═══════════════════════════════════════════
        if (flashAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(flashAlpha * 0.7f)
                    .background(Color.White)
            )
        }

        // ═══════════════════════════════════════════
        // TIMER COUNTDOWN
        // ═══════════════════════════════════════════
        timerCountdown?.let { seconds ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = seconds.toString(),
                    color = Color.White,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // ═══════════════════════════════════════════
        // TOP CONTROLS — Samsung One UI style
        // ═══════════════════════════════════════════
        AnimatedVisibility(
            visible = !isRecording,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Flash
                IconButton(onClick = {
                    haptics.lightTap()
                    viewModel.cycleFlashMode()
                }) {
                    Icon(
                        imageVector = when (config.flashMode) {
                            FlashMode.OFF -> Icons.Outlined.FlashOff
                            FlashMode.ON -> Icons.Filled.FlashOn
                            FlashMode.AUTO -> Icons.Outlined.FlashAuto
                            FlashMode.TORCH -> Icons.Filled.FlashOn
                        },
                        contentDescription = "Flash",
                        tint = if (config.flashMode == FlashMode.OFF) ColorTokens.TopBarIcon else ColorTokens.TopBarIconActive,
                    )
                }

                // Aspect Ratio
                IconButton(onClick = {
                    haptics.lightTap()
                    val ratios = listOf(AspectRatio.RATIO_3_4, AspectRatio.RATIO_9_16, AspectRatio.RATIO_1_1, AspectRatio.FULL)
                    val idx = ratios.indexOf(config.aspectRatio)
                    viewModel.setAspectRatio(ratios[(idx + 1) % ratios.size])
                }) {
                    Box(
                        modifier = Modifier
                            .background(Color.Transparent)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = config.aspectRatio.displayName,
                            color = ColorTokens.TopBarIcon,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                // Timer
                IconButton(onClick = {
                    haptics.lightTap()
                    val timers = TimerDuration.entries
                    val idx = timers.indexOf(config.timer)
                    viewModel.setTimer(timers[(idx + 1) % timers.size])
                }) {
                    Icon(
                        imageVector = when (config.timer) {
                            TimerDuration.OFF -> Icons.Outlined.TimerOff
                            TimerDuration.SEC_2 -> Icons.Outlined.Timer
                            TimerDuration.SEC_5 -> Icons.Outlined.Timer
                            TimerDuration.SEC_10 -> Icons.Outlined.Timer
                        },
                        contentDescription = "Timer: ${config.timer.displayName}",
                        tint = if (config.timer == TimerDuration.OFF) ColorTokens.TopBarIcon else ColorTokens.TopBarIconActive,
                    )
                }

                // Settings
                IconButton(onClick = {
                    haptics.lightTap()
                    onNavigateToSettings()
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                        tint = ColorTokens.TopBarIcon,
                    )
                }
            }
        }

        // ═══════════════════════════════════════════
        // RECORDING INDICATOR
        // ═══════════════════════════════════════════
        AnimatedVisibility(
            visible = isRecording,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 16.dp),
        ) {
            RecordingIndicator(duration = recordingDuration)
        }

        // ═══════════════════════════════════════════
        // ZOOM QUICK PILLS
        // ═══════════════════════════════════════════
        ZoomQuickPills(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 190.dp),
            currentZoom = zoomState.zoomRatio,
            onZoomSelected = { ratio ->
                haptics.zoomStop()
                viewModel.setZoomRatio(ratio)
            },
        )

        // ═══════════════════════════════════════════
        // MODE SELECTOR — Samsung One UI style
        // ═══════════════════════════════════════════
        CameraModeSelectorStrip(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp),
            currentMode = cameraMode,
            onModeSelected = { mode ->
                haptics.mediumImpact()
                viewModel.setMode(mode)
            },
        )

        // ═══════════════════════════════════════════
        // BOTTOM BAR — Capture controls
        // ═══════════════════════════════════════════
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp, start = 32.dp, end = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Gallery thumbnail
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorTokens.SurfaceElevated)
                    .clickable { onNavigateToGallery() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.PhotoLibrary,
                    "Gallery",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp),
                )
            }

            // CAPTURE BUTTON with iOS bounce animation
            com.beadrop.design.components.buttons.CaptureButton(
                modifier = Modifier.scale(captureButtonScale),
                isVideoMode = isVideoMode,
                isRecording = isRecording,
                onCapture = {
                    haptics.heavyImpact()
                    if (isVideoMode) {
                        if (isRecording) viewModel.stopVideoRecording()
                        else viewModel.startVideoRecording()
                    } else {
                        // iOS-style: flash + bounce
                        showCaptureFlash = true
                        captureScale = 0.85f
                        viewModel.capturePhoto()
                        // Bounce back
                        captureScale = 1f
                    }
                },
            )

            // Switch camera
            IconButton(
                onClick = {
                    haptics.mediumImpact()
                    viewModel.switchCamera()
                },
                modifier = Modifier
                    .size(52.dp)
                    .background(ColorTokens.SurfaceControl, CircleShape),
            ) {
                Icon(
                    Icons.Filled.Cameraswitch,
                    "Switch Camera",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp),
                )
            }
        }
    }
}
