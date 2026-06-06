package com.beadrop.camera.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beadrop.camera.capture.CaptureState
import com.beadrop.camera.capture.PhotoCaptureManager
import com.beadrop.camera.capture.RecordingState
import com.beadrop.camera.capture.VideoCaptureManager
import com.beadrop.camera.engine.CameraEngine
import com.beadrop.camera.engine.CameraEngineState
import com.beadrop.camera.zoom.ZoomController
import com.beadrop.core.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraEngine: CameraEngine,
    private val photoCaptureManager: PhotoCaptureManager,
    private val videoCaptureManager: VideoCaptureManager,
    private val zoomController: ZoomController,
) : ViewModel() {

    // ═══════════════════════════════════════════════
    // STATE
    // ═══════════════════════════════════════════════
    
    private val _cameraMode = MutableStateFlow(CameraMode.PHOTO)
    val cameraMode: StateFlow<CameraMode> = _cameraMode.asStateFlow()

    private val _config = MutableStateFlow(CameraConfig())
    val config: StateFlow<CameraConfig> = _config.asStateFlow()

    private val _proModeState = MutableStateFlow(ProModeState())
    val proModeState: StateFlow<ProModeState> = _proModeState.asStateFlow()

    private val _showGrid = MutableStateFlow(false)
    val showGrid: StateFlow<Boolean> = _showGrid.asStateFlow()

    private val _showLevel = MutableStateFlow(false)
    val showLevel: StateFlow<Boolean> = _showLevel.asStateFlow()

    private val _showHistogram = MutableStateFlow(false)
    val showHistogram: StateFlow<Boolean> = _showHistogram.asStateFlow()

    private val _showFocusPeaking = MutableStateFlow(false)
    val showFocusPeaking: StateFlow<Boolean> = _showFocusPeaking.asStateFlow()

    private val _showZebraStripes = MutableStateFlow(false)
    val showZebraStripes: StateFlow<Boolean> = _showZebraStripes.asStateFlow()

    private val _timerCountdown = MutableStateFlow<Int?>(null)
    val timerCountdown: StateFlow<Int?> = _timerCountdown.asStateFlow()

    private val _lastCapturedUri = MutableStateFlow<Uri?>(null)
    val lastCapturedUri: StateFlow<Uri?> = _lastCapturedUri.asStateFlow()

    // Combined UI state
    val uiState: StateFlow<CameraUiState> = combine(
        cameraEngine.cameraState,
        cameraEngine.zoomState,
        cameraEngine.focusState,
        cameraEngine.exposureState,
        _cameraMode,
    ) { engineState, zoomState, focusState, exposureState, mode ->
        CameraUiState(
            engineState = engineState,
            zoomState = zoomState,
            focusState = focusState,
            exposureState = exposureState,
            currentMode = mode,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CameraUiState(),
    )

    // Camera engine state
    val cameraState: StateFlow<CameraEngineState> = cameraEngine.cameraState
    val zoomState: StateFlow<ZoomState> = cameraEngine.zoomState
    val focusState: StateFlow<FocusState> = cameraEngine.focusState
    val exposureState: StateFlow<ExposureState> = cameraEngine.exposureState
    val isFrontCamera: StateFlow<Boolean> = cameraEngine.isFrontCamera
    val captureState: StateFlow<CaptureState> = photoCaptureManager.captureState
    val recordingState: StateFlow<RecordingState> = videoCaptureManager.recordingState
    val recordingDuration: StateFlow<Long> = videoCaptureManager.recordingDuration

    // Zoom
    val showNavigator: StateFlow<Boolean> = zoomController.showNavigator
    val showTargeting: StateFlow<Boolean> = zoomController.showTargeting
    val showStabilization: StateFlow<Boolean> = zoomController.showStabilization

    // ═══════════════════════════════════════════════
    // INITIALIZATION
    // ═══════════════════════════════════════════════

    fun initializeCamera() {
        viewModelScope.launch {
            cameraEngine.initialize()
        }
    }

    fun getCameraEngine(): CameraEngine = cameraEngine

    // ═══════════════════════════════════════════════
    // CAMERA MODE
    // ═══════════════════════════════════════════════

    fun setMode(mode: CameraMode) {
        _cameraMode.value = mode
    }

    // ═══════════════════════════════════════════════
    // CAPTURE
    // ═══════════════════════════════════════════════

    fun capturePhoto() {
        viewModelScope.launch {
            val timer = _config.value.timer
            if (timer != TimerDuration.OFF) {
                for (i in timer.seconds downTo 1) {
                    _timerCountdown.value = i
                    kotlinx.coroutines.delay(1000)
                }
                _timerCountdown.value = null
            }

            val imageCapture = cameraEngine.getImageCapture() ?: return@launch
            try {
                val uri = photoCaptureManager.capturePhoto(
                    imageCapture = imageCapture,
                    mirrorFront = _config.value.mirrorFrontCamera && isFrontCamera.value,
                )
                _lastCapturedUri.value = uri
            } catch (e: Exception) {
                // Error handled in captureState
            }
        }
    }

    fun startVideoRecording() {
        val videoCapture = cameraEngine.getVideoCapture() ?: return
        videoCaptureManager.startRecording(videoCapture)
    }

    fun stopVideoRecording() {
        videoCaptureManager.stopRecording()
    }

    fun pauseVideoRecording() {
        videoCaptureManager.pauseRecording()
    }

    fun resumeVideoRecording() {
        videoCaptureManager.resumeRecording()
    }

    // ═══════════════════════════════════════════════
    // ZOOM
    // ═══════════════════════════════════════════════

    fun setZoomRatio(ratio: Float) {
        zoomController.setZoom(ratio)
        cameraEngine.setZoomRatio(ratio)
    }

    fun onPinchZoom(scaleFactor: Float) {
        val currentZoom = zoomState.value.zoomRatio
        val newZoom = zoomController.onPinchZoom(scaleFactor, currentZoom)
        cameraEngine.setZoomRatio(newZoom)
    }

    fun nextZoomStop() {
        val newZoom = zoomController.nextZoomStop()
        cameraEngine.setZoomRatio(newZoom)
    }

    // ═══════════════════════════════════════════════
    // FOCUS
    // ═══════════════════════════════════════════════

    fun focusAtPoint(x: Float, y: Float, viewWidth: Int, viewHeight: Int) {
        cameraEngine.focusAtPoint(x, y, viewWidth, viewHeight)
    }

    fun lockFocus() {
        cameraEngine.lockFocus()
    }

    fun unlockFocus() {
        cameraEngine.unlockFocus()
    }

    // ═══════════════════════════════════════════════
    // EXPOSURE
    // ═══════════════════════════════════════════════

    fun setExposureCompensation(value: Int) {
        cameraEngine.setExposureCompensation(value)
    }

    // ═══════════════════════════════════════════════
    // SETTINGS
    // ═══════════════════════════════════════════════

    fun cycleFlashMode() {
        val modes = FlashMode.entries
        val currentIndex = modes.indexOf(_config.value.flashMode)
        val nextMode = modes[(currentIndex + 1) % modes.size]
        _config.value = _config.value.copy(flashMode = nextMode)
    }

    fun setAspectRatio(ratio: AspectRatio) {
        _config.value = _config.value.copy(aspectRatio = ratio)
    }

    fun setTimer(timer: TimerDuration) {
        _config.value = _config.value.copy(timer = timer)
    }

    fun toggleGrid() {
        val types = GridType.entries
        val currentIndex = types.indexOf(_config.value.gridType)
        val nextType = types[(currentIndex + 1) % types.size]
        _config.value = _config.value.copy(gridType = nextType)
        _showGrid.value = nextType != GridType.NONE
    }

    fun toggleHDR() {
        _config.value = _config.value.copy(hdrEnabled = !_config.value.hdrEnabled)
    }

    fun switchCamera() {
        cameraEngine.switchCamera()
    }

    fun toggleLevel() {
        _showLevel.value = !_showLevel.value
    }

    fun toggleHistogram() {
        _showHistogram.value = !_showHistogram.value
    }

    fun toggleFocusPeaking() {
        _showFocusPeaking.value = !_showFocusPeaking.value
    }

    fun toggleZebraStripes() {
        _showZebraStripes.value = !_showZebraStripes.value
    }

    // ═══════════════════════════════════════════════
    // PRO MODE
    // ═══════════════════════════════════════════════

    fun setManualISO(iso: Int) {
        _proModeState.value = _proModeState.value.copy(manualIso = iso)
    }

    fun setManualShutterSpeed(speed: Long) {
        _proModeState.value = _proModeState.value.copy(manualShutterSpeed = speed)
    }

    fun setManualFocusDistance(distance: Float) {
        _proModeState.value = _proModeState.value.copy(
            manualFocusEnabled = true,
            manualFocusDistance = distance,
        )
    }

    fun setWhiteBalance(wb: WhiteBalance) {
        _proModeState.value = _proModeState.value.copy(whiteBalance = wb)
    }

    // ═══════════════════════════════════════════════
    // CLEANUP
    // ═══════════════════════════════════════════════

    override fun onCleared() {
        super.onCleared()
        cameraEngine.release()
    }
}

data class CameraUiState(
    val engineState: CameraEngineState = CameraEngineState(),
    val zoomState: ZoomState = ZoomState(),
    val focusState: FocusState = FocusState(),
    val exposureState: ExposureState = ExposureState(),
    val currentMode: CameraMode = CameraMode.PHOTO,
)
