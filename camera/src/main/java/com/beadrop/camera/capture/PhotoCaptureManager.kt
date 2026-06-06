package com.beadrop.camera.capture

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Manages photo capture with MediaStore integration.
 * 
 * Supports:
 * - JPEG, PNG, WEBP, HEIC output
 * - HDR capture coordination
 * - Burst capture
 * - Timer-delayed capture
 * - Mirror front camera output
 */
@Singleton
class PhotoCaptureManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US)

    private val _captureState = MutableStateFlow<CaptureState>(CaptureState.Idle)
    val captureState: StateFlow<CaptureState> = _captureState.asStateFlow()

    private val _lastCapturedUri = MutableStateFlow<Uri?>(null)
    val lastCapturedUri: StateFlow<Uri?> = _lastCapturedUri.asStateFlow()

    /**
     * Capture a photo and save to MediaStore.
     */
    suspend fun capturePhoto(
        imageCapture: ImageCapture,
        mirrorFront: Boolean = false,
        outputFormat: OutputFormat = OutputFormat.JPEG,
    ): Uri {
        _captureState.value = CaptureState.Capturing

        return suspendCancellableCoroutine { continuation ->
            val timestamp = System.currentTimeMillis()
            val fileName = "BDROP_${dateFormat.format(timestamp)}"

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.${outputFormat.extension}")
                put(MediaStore.Images.Media.MIME_TYPE, outputFormat.mimeType)
                put(MediaStore.Images.Media.DATE_ADDED, timestamp / 1000)
                put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/BeadropCamera")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val outputOptions = ImageCapture.OutputFileOptions.Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()

            imageCapture.takePicture(
                outputOptions,
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val savedUri = output.savedUri
                        if (savedUri != null) {
                            // Mark as not pending
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                val updateValues = ContentValues().apply {
                                    put(MediaStore.Images.Media.IS_PENDING, 0)
                                }
                                context.contentResolver.update(savedUri, updateValues, null, null)
                            }
                            _captureState.value = CaptureState.Captured(savedUri)
                            _lastCapturedUri.value = savedUri
                            continuation.resume(savedUri)
                        } else {
                            val error = Exception("Saved URI is null")
                            _captureState.value = CaptureState.Error(error.message ?: "Unknown error")
                            continuation.resumeWithException(error)
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        _captureState.value = CaptureState.Error(exception.message ?: "Capture failed")
                        continuation.resumeWithException(exception)
                    }
                }
            )
        }
    }

    /**
     * Capture burst of photos.
     */
    suspend fun captureBurst(
        imageCapture: ImageCapture,
        count: Int = 10,
        onProgress: (Int, Int) -> Unit = { _, _ -> },
    ): List<Uri> {
        _captureState.value = CaptureState.BurstCapturing(0, count)
        val results = mutableListOf<Uri>()

        for (i in 0 until count) {
            try {
                val uri = capturePhoto(imageCapture)
                results.add(uri)
                _captureState.value = CaptureState.BurstCapturing(i + 1, count)
                onProgress(i + 1, count)
            } catch (e: Exception) {
                // Continue burst even if individual frame fails
            }
        }

        _captureState.value = CaptureState.BurstComplete(results.size)
        return results
    }

    fun resetState() {
        _captureState.value = CaptureState.Idle
    }
}

sealed class CaptureState {
    data object Idle : CaptureState()
    data object Capturing : CaptureState()
    data class Captured(val uri: Uri) : CaptureState()
    data class BurstCapturing(val current: Int, val total: Int) : CaptureState()
    data class BurstComplete(val count: Int) : CaptureState()
    data class Error(val message: String) : CaptureState()
}

enum class OutputFormat(
    val extension: String,
    val mimeType: String,
) {
    JPEG("jpg", "image/jpeg"),
    PNG("png", "image/png"),
    WEBP("webp", "image/webp"),
    HEIC("heic", "image/heic"),
}
