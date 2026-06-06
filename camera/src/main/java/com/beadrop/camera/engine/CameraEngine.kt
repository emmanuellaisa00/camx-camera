package com.beadrop.camera.engine

import android.content.Context
import android.util.Size
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.TorchState
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.lifecycle.LifecycleOwner
import com.beadrop.core.domain.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.ExecutionException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Beadrop Camera Engine
 * 
 * Advanced camera engine built on CameraX with Camera2 interop.
 * Manages camera lifecycle, use cases, focus, exposure, zoom, and capture.
 */
@Singleton
class CameraEngine @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    // Camera state
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo? = null

    // Use cases
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var videoCapture: VideoCapture<Recorder>? = null

    // State flows
    private val _cameraState = MutableStateFlow(CameraEngineState())
    val cameraState: StateFlow<CameraEngineState> = _cameraState.asStateFlow()

    private val _zoomState = MutableStateFlow(ZoomState())
    val zoomState: StateFlow<ZoomState> = _zoomState.asStateFlow()

    private val _focusState = MutableStateFlow(FocusState())
    val focusState: StateFlow<FocusState> = _focusState.asStateFlow()

    private val _exposureState = MutableStateFlow(ExposureState())
    val exposureState: StateFlow<ExposureState> = _exposureState.asStateFlow()

    private val _isFrontCamera = MutableStateFlow(false)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()

    /**
     * Initialize the camera system.
     */
    suspend fun initialize() {
        try {
            cameraProvider = suspendCancellableCoroutine { cont ->
                val future = ProcessCameraProvider.getInstance(context)
                future.addListener({
                    try {
                        cont.resume(future.get())
                    } catch (e: ExecutionException) {
                        cont.resumeWithException(e.cause ?: e)
                    } catch (e: Exception) {
                        cont.resumeWithException(e)
                    }
                }, { it.run() })
            }
            _cameraState.value = _cameraState.value.copy(isInitialized = true)
        } catch (e: Exception) {
            _cameraState.value = _cameraState.value.copy(
                error = "Failed to initialize camera: ${e.message}"
            )
        }
    }

    /**
     * Bind camera use cases to lifecycle.
     */
    fun bindCamera(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider,
        mode: CameraMode,
        config: CameraConfig,
    ) {
        val provider = cameraProvider ?: return

        try {
            // Unbind all previous
            provider.unbindAll()

            // Camera selector
            val cameraSelector = if (_isFrontCamera.value) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            // Build Preview
            preview = Preview.Builder()
                .build()
                .also { it.surfaceProvider = surfaceProvider }

            // Build ImageCapture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setFlashMode(
                    when (config.flashMode) {
                        FlashMode.OFF -> ImageCapture.FLASH_MODE_OFF
                        FlashMode.ON -> ImageCapture.FLASH_MODE_ON
                        FlashMode.AUTO -> ImageCapture.FLASH_MODE_AUTO
                        FlashMode.TORCH -> ImageCapture.FLASH_MODE_OFF
                    }
                )
                .build()

            // Build ImageAnalysis for AI features
            imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build()

            // Build VideoCapture if needed
            val useCaseGroupBuilder = UseCaseGroup.Builder()
                .addUseCase(preview!!)
                .addUseCase(imageCapture!!)

            if (mode.supportsVideo || mode == CameraMode.VIDEO) {
                val qualitySelector = QualitySelector.from(
                    when (config.videoResolution) {
                        VideoResolution.HD_720P -> Quality.HD
                        VideoResolution.FHD_1080P -> Quality.FHD
                        VideoResolution.UHD_4K -> Quality.UHD
                        VideoResolution.UHD_8K -> Quality.HIGHEST
                    }
                )

                val recorder = Recorder.Builder()
                    .setQualitySelector(qualitySelector)
                    .setExecutor(cameraExecutor)
                    .build()

                videoCapture = VideoCapture.withOutput(recorder)
                useCaseGroupBuilder.addUseCase(videoCapture!!)
            } else {
                useCaseGroupBuilder.addUseCase(imageAnalysis!!)
            }

            // Bind to lifecycle
            val useCases = useCaseGroupBuilder.build().useCases
            camera = when (useCases.size) {
                1 -> provider.bindToLifecycle(lifecycleOwner, cameraSelector, useCases[0])
                2 -> provider.bindToLifecycle(lifecycleOwner, cameraSelector, useCases[0], useCases[1])
                3 -> provider.bindToLifecycle(lifecycleOwner, cameraSelector, useCases[0], useCases[1], useCases[2])
                else -> provider.bindToLifecycle(lifecycleOwner, cameraSelector, preview!!)
            }

            cameraControl = camera?.cameraControl
            cameraInfo = camera?.cameraInfo

            // Setup zoom state observer
            setupZoomObserver()
            setupExposureObserver()

            // Apply torch mode
            if (config.flashMode == FlashMode.TORCH) {
                cameraControl?.enableTorch(true)
            }

            _cameraState.value = _cameraState.value.copy(
                isBound = true,
                currentMode = mode,
                error = null,
            )

        } catch (e: Exception) {
            _cameraState.value = _cameraState.value.copy(
                error = "Failed to bind camera: ${e.message}"
            )
        }
    }

    private fun setupZoomObserver() {
        cameraInfo?.zoomState?.observeForever { zoom ->
            _zoomState.value = ZoomState(
                zoomRatio = zoom.zoomRatio,
                minZoomRatio = zoom.minZoomRatio,
                maxZoomRatio = zoom.maxZoomRatio,
                linearZoom = zoom.linearZoom,
            )
        }
    }

    private fun setupExposureObserver() {
        val info = cameraInfo ?: return
        val range = info.exposureState.exposureCompensationRange
        val step = info.exposureState.exposureCompensationStep.toFloat()
        _exposureState.value = ExposureState(
            exposureRange = range.lower..range.upper,
            exposureStep = step,
        )
    }

    /**
     * Set zoom ratio with smooth animation.
     */
    fun setZoomRatio(ratio: Float) {
        val clampedRatio = ratio.coerceIn(
            _zoomState.value.minZoomRatio,
            _zoomState.value.maxZoomRatio
        )
        cameraControl?.setZoomRatio(clampedRatio)
    }

    /**
     * Set linear zoom (0f to 1f).
     */
    fun setLinearZoom(zoom: Float) {
        cameraControl?.setLinearZoom(zoom.coerceIn(0f, 1f))
    }

    /**
     * Tap to focus at coordinates.
     */
    fun focusAtPoint(x: Float, y: Float, viewWidth: Int, viewHeight: Int) {
        val factory = SurfaceOrientedMeteringPointFactory(
            viewWidth.toFloat(), viewHeight.toFloat()
        )
        val point = factory.createPoint(x, y)

        val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE)
            .setAutoCancelDuration(3, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        _focusState.value = FocusState(
            isFocused = false,
            focusX = x / viewWidth,
            focusY = y / viewHeight,
            isLocked = false,
        )

        cameraControl?.startFocusAndMetering(action)?.addListener({
            _focusState.value = _focusState.value.copy(isFocused = true)
        }, cameraExecutor)
    }

    /**
     * Lock focus at current position.
     */
    fun lockFocus() {
        _focusState.value = _focusState.value.copy(isLocked = true)
    }

    /**
     * Unlock focus.
     */
    fun unlockFocus() {
        _focusState.value = _focusState.value.copy(isLocked = false)
        cameraControl?.cancelFocusAndMetering()
    }

    /**
     * Set exposure compensation.
     */
    fun setExposureCompensation(value: Int) {
        val range = _exposureState.value.exposureRange
        val clamped = value.coerceIn(range)
        cameraControl?.setExposureCompensationIndex(clamped)
        _exposureState.value = _exposureState.value.copy(exposureCompensation = clamped)
    }

    /**
     * Lock exposure.
     */
    fun lockExposure() {
        _exposureState.value = _exposureState.value.copy(isLocked = true)
    }

    /**
     * Unlock exposure.
     */
    fun unlockExposure() {
        _exposureState.value = _exposureState.value.copy(isLocked = false)
    }

    /**
     * Toggle torch.
     */
    fun setTorch(enabled: Boolean) {
        cameraControl?.enableTorch(enabled)
    }

    /**
     * Switch between front and back camera.
     */
    fun switchCamera() {
        _isFrontCamera.value = !_isFrontCamera.value
    }

    /**
     * Get image capture use case for taking photos.
     */
    fun getImageCapture(): ImageCapture? = imageCapture

    /**
     * Get video capture use case for recording.
     */
    fun getVideoCapture(): VideoCapture<Recorder>? = videoCapture

    /**
     * Get image analysis use case.
     */
    fun getImageAnalysis(): ImageAnalysis? = imageAnalysis

    /**
     * Release camera resources.
     */
    fun release() {
        cameraProvider?.unbindAll()
        cameraExecutor.shutdown()
        camera = null
        cameraControl = null
        cameraInfo = null
    }
}

data class CameraEngineState(
    val isInitialized: Boolean = false,
    val isBound: Boolean = false,
    val currentMode: CameraMode = CameraMode.PHOTO,
    val isCapturing: Boolean = false,
    val isRecording: Boolean = false,
    val recordingDuration: Long = 0L,
    val error: String? = null,
)
