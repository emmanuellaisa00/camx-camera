package com.beadrop.ai.detection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline face detection using TensorFlow Lite.
 * 
 * Uses BlazeFace or similar lightweight model for real-time detection.
 * All processing is on-device — no network required.
 */
@Singleton
class FaceDetector @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var interpreter: Interpreter? = null
    private val inputSize = 128
    private val numDetections = 896 // BlazeFace outputs
    private val isInitialized get() = interpreter != null

    data class FaceDetection(
        val boundingBox: RectF,
        val confidence: Float,
        val landmarks: List<Pair<Float, Float>>,
    )

    /**
     * Initialize the face detection model.
     */
    suspend fun initialize() = withContext(Dispatchers.IO) {
        try {
            val model = loadModelFile("face_detection.tflite")
            val options = Interpreter.Options().apply {
                setNumThreads(4)
                // GPU delegate would be added for production:
                // addDelegate(GpuDelegate())
            }
            interpreter = Interpreter(model, options)
        } catch (e: Exception) {
            // Model file not bundled yet — this is expected during scaffolding
            // In production, the .tflite model would be in assets/
        }
    }

    /**
     * Detect faces in a bitmap.
     */
    suspend fun detectFaces(bitmap: Bitmap): List<FaceDetection> = withContext(Dispatchers.Default) {
        val interp = interpreter ?: return@withContext emptyList()

        // Preprocess: resize to input size
        val resized = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val inputBuffer = preprocessBitmap(resized)

        // Output buffers
        val outputScores = Array(1) { FloatArray(numDetections) }
        val outputBoxes = Array(1) { Array(numDetections) { FloatArray(4) } }

        try {
            val outputs = mapOf(
                0 to outputBoxes,
                1 to outputScores,
            )
            interp.runForMultipleInputsOutputs(arrayOf(inputBuffer), outputs)

            // Post-process: filter by confidence and apply NMS
            val detections = mutableListOf<FaceDetection>()
            for (i in 0 until numDetections) {
                val score = outputScores[0][i]
                if (score > 0.5f) {
                    val box = outputBoxes[0][i]
                    detections.add(
                        FaceDetection(
                            boundingBox = RectF(box[1], box[0], box[3], box[2]),
                            confidence = score,
                            landmarks = emptyList(),
                        )
                    )
                }
            }

            nonMaxSuppression(detections, 0.3f)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Release resources.
     */
    fun release() {
        interpreter?.close()
        interpreter = null
    }

    private fun preprocessBitmap(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4)
        buffer.order(ByteOrder.nativeOrder())
        buffer.rewind()

        val pixels = IntArray(inputSize * inputSize)
        bitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixel in pixels) {
            val r = ((pixel shr 16) and 0xFF) / 255f
            val g = ((pixel shr 8) and 0xFF) / 255f
            val b = (pixel and 0xFF) / 255f
            buffer.putFloat(r)
            buffer.putFloat(g)
            buffer.putFloat(b)
        }

        return buffer
    }

    private fun nonMaxSuppression(
        detections: List<FaceDetection>,
        iouThreshold: Float,
    ): List<FaceDetection> {
        if (detections.isEmpty()) return emptyList()

        val sorted = detections.sortedByDescending { it.confidence }
        val selected = mutableListOf<FaceDetection>()

        val active = BooleanArray(sorted.size) { true }
        for (i in sorted.indices) {
            if (!active[i]) continue
            selected.add(sorted[i])
            for (j in i + 1 until sorted.size) {
                if (active[j] && computeIoU(sorted[i].boundingBox, sorted[j].boundingBox) > iouThreshold) {
                    active[j] = false
                }
            }
        }
        return selected
    }

    private fun computeIoU(a: RectF, b: RectF): Float {
        val intersect = RectF()
        if (!intersect.setIntersect(a, b)) return 0f
        val intersectArea = intersect.width() * intersect.height()
        val unionArea = a.width() * a.height() + b.width() * b.height() - intersectArea
        return if (unionArea > 0) intersectArea / unionArea else 0f
    }

    private fun loadModelFile(filename: String): MappedByteBuffer {
        val assetFd = context.assets.openFd(filename)
        val inputStream = FileInputStream(assetFd.fileDescriptor)
        val channel = inputStream.channel
        return channel.map(FileChannel.MapMode.READ_ONLY, assetFd.startOffset, assetFd.declaredLength)
    }
}
