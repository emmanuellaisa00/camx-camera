package com.beadrop.editor.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import com.beadrop.core.domain.model.ColorChannel
import com.beadrop.core.domain.model.EditAction
import com.beadrop.core.domain.model.EditHistory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * GPU-accelerated image editing engine.
 * 
 * Applies edits non-destructively using an action stack.
 * All operations are performed on-device with no network access.
 */
@Singleton
class EditEngine @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val _editHistory = MutableStateFlow(EditHistory())
    val editHistory: StateFlow<EditHistory> = _editHistory.asStateFlow()

    private var originalBitmap: Bitmap? = null
    private var currentBitmap: Bitmap? = null

    /**
     * Load an image for editing.
     */
    suspend fun loadImage(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            originalBitmap = bitmap
            currentBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
            _editHistory.value = EditHistory()
            currentBitmap
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Apply an edit action.
     */
    suspend fun applyAction(action: EditAction): Bitmap? = withContext(Dispatchers.Default) {
        val original = originalBitmap ?: return@withContext null
        _editHistory.value = _editHistory.value.addAction(action)

        // Rebuild from original applying all active actions
        var result = original.copy(Bitmap.Config.ARGB_8888, true)
        for (activeAction in _editHistory.value.activeActions) {
            result = applyEdit(result, activeAction) ?: result
        }

        currentBitmap = result
        result
    }

    /**
     * Undo last action.
     */
    suspend fun undo(): Bitmap? = withContext(Dispatchers.Default) {
        _editHistory.value = _editHistory.value.undo()
        rebuildFromHistory()
    }

    /**
     * Redo last undone action.
     */
    suspend fun redo(): Bitmap? = withContext(Dispatchers.Default) {
        _editHistory.value = _editHistory.value.redo()
        rebuildFromHistory()
    }

    /**
     * Reset all edits.
     */
    fun reset(): Bitmap? {
        _editHistory.value = _editHistory.value.clear()
        currentBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
        return currentBitmap
    }

    /**
     * Get current edited bitmap.
     */
    fun getCurrentBitmap(): Bitmap? = currentBitmap

    /**
     * Get original bitmap.
     */
    fun getOriginalBitmap(): Bitmap? = originalBitmap

    private suspend fun rebuildFromHistory(): Bitmap? {
        val original = originalBitmap ?: return null
        var result = original.copy(Bitmap.Config.ARGB_8888, true)
        for (action in _editHistory.value.activeActions) {
            result = applyEdit(result, action) ?: result
        }
        currentBitmap = result
        return result
    }

    private fun applyEdit(bitmap: Bitmap, action: EditAction): Bitmap? {
        return when (action) {
            is EditAction.Crop -> cropBitmap(bitmap, action)
            is EditAction.Rotate -> rotateBitmap(bitmap, action.degrees)
            is EditAction.Exposure -> adjustExposure(bitmap, action.value)
            is EditAction.Contrast -> adjustContrast(bitmap, action.value)
            is EditAction.Saturation -> adjustSaturation(bitmap, action.value)
            is EditAction.Brightness -> adjustBrightness(bitmap, action.value)
            is EditAction.Sharpness -> adjustSharpness(bitmap, action.value)
            is EditAction.Blur -> applyBlur(bitmap, action.radius)
            is EditAction.Vignette -> applyVignette(bitmap, action.intensity)
            is EditAction.Filter -> applyFilter(bitmap, action.filterId, action.intensity)
            else -> bitmap
        }
    }

    private fun cropBitmap(bitmap: Bitmap, crop: EditAction.Crop): Bitmap {
        val x = (crop.left * bitmap.width).toInt().coerceIn(0, bitmap.width)
        val y = (crop.top * bitmap.height).toInt().coerceIn(0, bitmap.height)
        val w = ((crop.right - crop.left) * bitmap.width).toInt().coerceIn(1, bitmap.width - x)
        val h = ((crop.bottom - crop.top) * bitmap.height).toInt().coerceIn(1, bitmap.height - y)
        return Bitmap.createBitmap(bitmap, x, y, w, h)
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun adjustExposure(bitmap: Bitmap, value: Float): Bitmap {
        val factor = 1f + value
        val cm = ColorMatrix().apply {
            setScale(factor, factor, factor, 1f)
        }
        return applyColorMatrix(bitmap, cm)
    }

    private fun adjustContrast(bitmap: Bitmap, value: Float): Bitmap {
        val factor = 1f + value
        val translate = (-0.5f * factor + 0.5f) * 255f
        val cm = ColorMatrix(floatArrayOf(
            factor, 0f, 0f, 0f, translate,
            0f, factor, 0f, 0f, translate,
            0f, 0f, factor, 0f, translate,
            0f, 0f, 0f, 1f, 0f,
        ))
        return applyColorMatrix(bitmap, cm)
    }

    private fun adjustSaturation(bitmap: Bitmap, value: Float): Bitmap {
        val cm = ColorMatrix().apply { setSaturation(1f + value) }
        return applyColorMatrix(bitmap, cm)
    }

    private fun adjustBrightness(bitmap: Bitmap, value: Float): Bitmap {
        val brightness = value * 255f
        val cm = ColorMatrix(floatArrayOf(
            1f, 0f, 0f, 0f, brightness,
            0f, 1f, 0f, 0f, brightness,
            0f, 0f, 1f, 0f, brightness,
            0f, 0f, 0f, 1f, 0f,
        ))
        return applyColorMatrix(bitmap, cm)
    }

    private fun adjustSharpness(bitmap: Bitmap, value: Float): Bitmap {
        // Unsharp mask approach
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        result.getPixels(pixels, 0, width, 0, 0, width, height)

        val blurred = IntArray(pixels.size)
        System.arraycopy(pixels, 0, blurred, 0, pixels.size)

        // Box blur
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                var sr = 0; var sg = 0; var sb = 0
                for (dy in -1..1) for (dx in -1..1) {
                    val p = pixels[(y + dy) * width + (x + dx)]
                    sr += (p shr 16) and 0xFF
                    sg += (p shr 8) and 0xFF
                    sb += p and 0xFF
                }
                blurred[y * width + x] = (0xFF shl 24) or
                    ((sr / 9) shl 16) or ((sg / 9) shl 8) or (sb / 9)
            }
        }

        for (i in pixels.indices) {
            val a = (pixels[i] shr 24) and 0xFF
            val r = (((pixels[i] shr 16) and 0xFF) + value * (((pixels[i] shr 16) and 0xFF) - ((blurred[i] shr 16) and 0xFF))).toInt().coerceIn(0, 255)
            val g = (((pixels[i] shr 8) and 0xFF) + value * (((pixels[i] shr 8) and 0xFF) - ((blurred[i] shr 8) and 0xFF))).toInt().coerceIn(0, 255)
            val b = ((pixels[i] and 0xFF) + value * ((pixels[i] and 0xFF) - (blurred[i] and 0xFF))).toInt().coerceIn(0, 255)
            pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }

        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    private fun applyBlur(bitmap: Bitmap, radius: Float): Bitmap {
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val iterations = (radius * 3).toInt().coerceIn(1, 10)
        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        result.getPixels(pixels, 0, width, 0, 0, width, height)

        for (iter in 0 until iterations) {
            val temp = IntArray(pixels.size)
            for (y in 1 until height - 1) {
                for (x in 1 until width - 1) {
                    var sr = 0; var sg = 0; var sb = 0
                    for (dy in -1..1) for (dx in -1..1) {
                        val p = pixels[(y + dy) * width + (x + dx)]
                        sr += (p shr 16) and 0xFF
                        sg += (p shr 8) and 0xFF
                        sb += p and 0xFF
                    }
                    val a = (pixels[y * width + x] shr 24) and 0xFF
                    temp[y * width + x] = (a shl 24) or
                        ((sr / 9) shl 16) or ((sg / 9) shl 8) or (sb / 9)
                }
            }
            System.arraycopy(temp, 0, pixels, 0, pixels.size)
        }

        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    private fun applyVignette(bitmap: Bitmap, intensity: Float): Bitmap {
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        result.getPixels(pixels, 0, width, 0, 0, width, height)

        val cx = width / 2f
        val cy = height / 2f
        val maxDist = kotlin.math.sqrt((cx * cx + cy * cy).toDouble()).toFloat()

        for (y in 0 until height) {
            for (x in 0 until width) {
                val dx = x - cx
                val dy = y - cy
                val dist = kotlin.math.sqrt((dx * dx + dy * dy).toDouble()).toFloat() / maxDist
                val factor = (1f - dist * dist * intensity).coerceIn(0f, 1f)

                val i = y * width + x
                val pixel = pixels[i]
                val a = (pixel shr 24) and 0xFF
                val r = ((pixel shr 16) and 0xFF) * factor
                val g = ((pixel shr 8) and 0xFF) * factor
                val b = (pixel and 0xFF) * factor

                pixels[i] = (a shl 24) or
                    (r.toInt().coerceIn(0, 255) shl 16) or
                    (g.toInt().coerceIn(0, 255) shl 8) or
                    b.toInt().coerceIn(0, 255)
            }
        }

        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    private fun applyFilter(bitmap: Bitmap, filterId: String, intensity: Float): Bitmap {
        val matrix = when (filterId) {
            "noir" -> ColorMatrix(floatArrayOf(
                0.5f, 0.5f, 0.5f, 0f, -30f,
                0.3f, 0.3f, 0.3f, 0f, -10f,
                0.2f, 0.2f, 0.2f, 0f, 10f,
                0f, 0f, 0f, 1f, 0f,
            ))
            "vintage" -> ColorMatrix(floatArrayOf(
                0.9f, 0.1f, 0f, 0f, 20f,
                0f, 0.8f, 0.1f, 0f, 10f,
                0f, 0.1f, 0.7f, 0f, -10f,
                0f, 0f, 0f, 1f, 0f,
            ))
            "cool" -> ColorMatrix(floatArrayOf(
                0.9f, 0f, 0f, 0f, 0f,
                0f, 0.9f, 0f, 0f, 0f,
                0f, 0f, 1.2f, 0f, 10f,
                0f, 0f, 0f, 1f, 0f,
            ))
            "warm" -> ColorMatrix(floatArrayOf(
                1.2f, 0f, 0f, 0f, 10f,
                0f, 1.0f, 0f, 0f, 5f,
                0f, 0f, 0.8f, 0f, -10f,
                0f, 0f, 0f, 1f, 0f,
            ))
            else -> ColorMatrix() // Identity
        }

        // Blend with identity based on intensity
        val identityArr = FloatArray(20).also { ColorMatrix().getArray().copyInto(it) }
        val matrixArr = FloatArray(20).also { matrix.getArray().copyInto(it) }
        val blendedArr = FloatArray(20)
        for (i in 0 until 20) {
            blendedArr[i] = identityArr[i] + (matrixArr[i] - identityArr[i]) * intensity
        }
        val blended = ColorMatrix(blendedArr)

        return applyColorMatrix(bitmap, blended)
    }

    private fun applyColorMatrix(bitmap: Bitmap, matrix: ColorMatrix): Bitmap {
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(matrix)
        }
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }
}
