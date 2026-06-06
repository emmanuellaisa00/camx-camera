package com.beadrop.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class CameraMode(
    val displayName: String,
    val shortName: String,
    val description: String,
    val iconName: String,
    val supportsVideo: Boolean = false,
    val requiresSpecialHardware: Boolean = false,
) {
    PHOTO(
        displayName = "Photo",
        shortName = "PHOTO",
        description = "Standard photo capture with auto optimization",
        iconName = "camera",
    ),
    VIDEO(
        displayName = "Video",
        shortName = "VIDEO",
        description = "High quality video recording",
        iconName = "videocam",
        supportsVideo = true,
    ),
    PORTRAIT(
        displayName = "Portrait",
        shortName = "PORTRAIT",
        description = "Depth-of-field portrait photography",
        iconName = "portrait",
    ),
    NIGHT(
        displayName = "Night",
        shortName = "NIGHT",
        description = "Enhanced low-light photography",
        iconName = "nightlight",
    ),
    PRO(
        displayName = "Pro",
        shortName = "PRO",
        description = "Full manual camera controls",
        iconName = "tune",
    ),
    PANORAMA(
        displayName = "Panorama",
        shortName = "PANO",
        description = "Wide panoramic photography",
        iconName = "panorama",
    ),
    SLOW_MOTION(
        displayName = "Slow Motion",
        shortName = "SLO-MO",
        description = "High frame rate slow motion video",
        iconName = "slow_motion_video",
        supportsVideo = true,
        requiresSpecialHardware = true,
    ),
    TIMELAPSE(
        displayName = "Timelapse",
        shortName = "LAPSE",
        description = "Time-lapse video recording",
        iconName = "timelapse",
        supportsVideo = true,
    ),
    HYPERLAPSE(
        displayName = "Hyperlapse",
        shortName = "HYPER",
        description = "Stabilized moving time-lapse",
        iconName = "speed",
        supportsVideo = true,
    ),
    MACRO(
        displayName = "Macro",
        shortName = "MACRO",
        description = "Close-up macro photography",
        iconName = "filter_center_focus",
    ),
    DOCUMENT(
        displayName = "Document",
        shortName = "DOC",
        description = "Document scanning with perspective correction",
        iconName = "document_scanner",
    );

    companion object {
        val defaultOrder = listOf(
            PHOTO, VIDEO, PORTRAIT, NIGHT, PRO,
            PANORAMA, SLOW_MOTION, TIMELAPSE, HYPERLAPSE,
            MACRO, DOCUMENT
        )

        val quickModes = listOf(PHOTO, VIDEO, PORTRAIT, NIGHT)
    }
}
