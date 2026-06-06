package com.beadrop.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class CameraMode(
    val displayName: String,
    val shortName: String,
    val supportsVideo: Boolean = false,
) {
    PHOTO(
        displayName = "Photo",
        shortName = "PHOTO",
    ),
    VIDEO(
        displayName = "Video",
        shortName = "VIDEO",
        supportsVideo = true,
    ),
    PORTRAIT(
        displayName = "Portrait",
        shortName = "PORTRAIT",
    ),
    NIGHT(
        displayName = "Night",
        shortName = "NIGHT",
    );

    companion object {
        val defaultOrder = listOf(NIGHT, PORTRAIT, PHOTO, VIDEO)
    }
}
