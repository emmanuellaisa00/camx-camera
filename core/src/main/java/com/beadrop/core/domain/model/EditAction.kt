package com.beadrop.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class EditAction {
    @Serializable data class Crop(val left: Float, val top: Float, val right: Float, val bottom: Float) : EditAction()
    @Serializable data class Rotate(val degrees: Float) : EditAction()
    @Serializable data class PerspectiveCorrection(val topLeft: Point, val topRight: Point, val bottomLeft: Point, val bottomRight: Point) : EditAction()
    @Serializable data class Exposure(val value: Float) : EditAction()
    @Serializable data class Contrast(val value: Float) : EditAction()
    @Serializable data class Saturation(val value: Float) : EditAction()
    @Serializable data class WhiteBalanceAdjust(val temperature: Float, val tint: Float) : EditAction()
    @Serializable data class Brightness(val value: Float) : EditAction()
    @Serializable data class Highlights(val value: Float) : EditAction()
    @Serializable data class Shadows(val value: Float) : EditAction()
    @Serializable data class Sharpness(val value: Float) : EditAction()
    @Serializable data class Blur(val radius: Float) : EditAction()
    @Serializable data class PortraitBlur(val intensity: Float) : EditAction()
    @Serializable data class Vignette(val intensity: Float) : EditAction()
    @Serializable data class Grain(val intensity: Float) : EditAction()
    @Serializable data class Filter(val filterId: String, val intensity: Float) : EditAction()
    @Serializable data class Curves(val channel: ColorChannel, val points: List<Point>) : EditAction()
    @Serializable data object AIEnhance : EditAction()
    @Serializable data object AutoAdjust : EditAction()
}

@Serializable
data class Point(val x: Float, val y: Float)

@Serializable
enum class ColorChannel {
    RGB, RED, GREEN, BLUE
}

@Serializable
data class EditHistory(
    val actions: List<EditAction> = emptyList(),
    val currentIndex: Int = -1,
) {
    val canUndo: Boolean get() = currentIndex >= 0
    val canRedo: Boolean get() = currentIndex < actions.lastIndex

    fun addAction(action: EditAction): EditHistory {
        val newActions = actions.take(currentIndex + 1) + action
        return EditHistory(newActions, newActions.lastIndex)
    }

    fun undo(): EditHistory = if (canUndo) copy(currentIndex = currentIndex - 1) else this
    fun redo(): EditHistory = if (canRedo) copy(currentIndex = currentIndex + 1) else this
    fun clear(): EditHistory = EditHistory()

    val activeActions: List<EditAction>
        get() = actions.take(currentIndex + 1)
}
