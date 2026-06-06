package com.beadrop.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations for Beadrop Camera.
 */
sealed interface BeadropDestination {

    @Serializable
    data object Camera : BeadropDestination

    @Serializable
    data object Gallery : BeadropDestination

    @Serializable
    data class ImageViewer(
        val mediaId: Long,
        val uri: String,
    ) : BeadropDestination

    @Serializable
    data class VideoPlayer(
        val mediaId: Long,
        val uri: String,
    ) : BeadropDestination

    @Serializable
    data class Editor(
        val mediaId: Long,
        val uri: String,
    ) : BeadropDestination

    @Serializable
    data object Settings : BeadropDestination

    @Serializable
    data class Album(
        val albumId: String,
        val albumName: String,
    ) : BeadropDestination

    @Serializable
    data object Favorites : BeadropDestination

    @Serializable
    data object RecycleBin : BeadropDestination
}
