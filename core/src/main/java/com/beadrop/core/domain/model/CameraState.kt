package com.beadrop.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CameraConfig(
    val flashMode: FlashMode = FlashMode.AUTO,
    val aspectRatio: AspectRatio = AspectRatio.RATIO_4_3,
    val timer: TimerDuration = TimerDuration.OFF,
    val gridType: GridType = GridType.NONE,
    val hdrEnabled: Boolean = false,
    val mirrorFrontCamera: Boolean = true,
    val geoTagging: Boolean = false,
    val shutterSound: Boolean = true,
    val qualityMode: QualityMode = QualityMode.HIGH,
    val videoResolution: VideoResolution = VideoResolution.UHD_4K,
    val videoFrameRate: VideoFrameRate = VideoFrameRate.FPS_30,
    val videoStabilization: Boolean = true,
)

@Serializable
enum class FlashMode(val displayName: String) {
    OFF("Off"),
    ON("On"),
    AUTO("Auto"),
    TORCH("Torch"),
}

@Serializable
enum class AspectRatio(val displayName: String, val ratioX: Int, val ratioY: Int) {
    RATIO_1_1("1:1", 1, 1),
    RATIO_3_4("3:4", 3, 4),
    RATIO_4_3("4:3", 4, 3),
    RATIO_9_16("9:16", 9, 16),
    RATIO_16_9("16:9", 16, 9),
    FULL("Full", 0, 0),
}

@Serializable
enum class TimerDuration(val seconds: Int, val displayName: String) {
    OFF(0, "Off"),
    SEC_2(2, "2s"),
    SEC_5(5, "5s"),
    SEC_10(10, "10s"),
}

@Serializable
enum class GridType(val displayName: String) {
    NONE("None"),
    RULE_OF_THIRDS("3×3"),
    GRID_4X4("4×4"),
    GOLDEN_RATIO("Golden"),
    CROSSHAIR("Crosshair"),
}

@Serializable
enum class QualityMode(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    MAXIMUM("Maximum"),
}

@Serializable
enum class VideoResolution(val displayName: String, val width: Int, val height: Int) {
    HD_720P("720p", 1280, 720),
    FHD_1080P("1080p", 1920, 1080),
    UHD_4K("4K", 3840, 2160),
    UHD_8K("8K", 7680, 4320),
}

@Serializable
enum class VideoFrameRate(val fps: Int, val displayName: String) {
    FPS_24(24, "24fps"),
    FPS_30(30, "30fps"),
    FPS_60(60, "60fps"),
    FPS_120(120, "120fps"),
    FPS_240(240, "240fps"),
}

@Serializable
data class ZoomState(
    val zoomRatio: Float = 1f,
    val minZoomRatio: Float = 0.5f,
    val maxZoomRatio: Float = 100f,
    val linearZoom: Float = 0f,
) {
    val displayZoom: String
        get() = when {
            zoomRatio < 1f -> "%.1fx".format(zoomRatio)
            zoomRatio < 10f -> "%.1fx".format(zoomRatio)
            else -> "%.0fx".format(zoomRatio)
        }

    val isUltraWide: Boolean get() = zoomRatio < 1f
    val isWide: Boolean get() = zoomRatio in 1f..2f
    val isTelephoto: Boolean get() = zoomRatio > 2f
    val isSuperZoom: Boolean get() = zoomRatio >= 10f
    val isHyperZoom: Boolean get() = zoomRatio >= 30f
    val isMaxZoom: Boolean get() = zoomRatio >= 100f
}

@Serializable
data class FocusState(
    val isFocused: Boolean = false,
    val focusX: Float = 0f,
    val focusY: Float = 0f,
    val isLocked: Boolean = false,
    val focusDistance: Float = 0f,
)

@Serializable
data class ExposureState(
    val exposureCompensation: Int = 0,
    val exposureRange: IntRange = -12..12,
    val exposureStep: Float = 0.166f,
    val isLocked: Boolean = false,
    val iso: Int = 0,
    val shutterSpeed: Long = 0L,
    val aperture: Float = 0f,
)

@Serializable
data class ProModeState(
    val manualFocusEnabled: Boolean = false,
    val manualFocusDistance: Float = 0f,
    val manualIso: Int = 100,
    val manualShutterSpeed: Long = 1000000L,
    val whiteBalance: WhiteBalance = WhiteBalance.AUTO,
    val whiteBalanceTemperature: Int = 5500,
)

@Serializable
enum class WhiteBalance(val displayName: String, val kelvin: Int) {
    AUTO("Auto", 0),
    INCANDESCENT("Incandescent", 2700),
    FLUORESCENT("Fluorescent", 4000),
    DAYLIGHT("Daylight", 5500),
    CLOUDY("Cloudy", 6500),
    SHADE("Shade", 7500),
    CUSTOM("Custom", 0),
}
