package com.beadrop.camera.capture

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore

import androidx.camera.video.FileOutputOptions
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.PermissionChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages video recording with MediaStore integration.
 */
@Singleton
class VideoCaptureManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    private var currentRecording: Recording? = null

    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)
    val recordingState: StateFlow<RecordingState> = _recordingState.asStateFlow()

    private val _recordingDuration = MutableStateFlow(0L)
    val recordingDuration: StateFlow<Long> = _recordingDuration.asStateFlow()

    /**
     * Start video recording.
     */
    @Suppress("MissingPermission")
    fun startRecording(
        videoCapture: VideoCapture<Recorder>,
        withAudio: Boolean = true,
    ) {
        val timestamp = System.currentTimeMillis()
        val fileName = "BDROP_VID_${dateFormat.format(timestamp)}"

        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "$fileName.mp4")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.DATE_ADDED, timestamp / 1000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/BeadropCamera")
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
        }

        val outputOptions = MediaStoreOutputOptions.Builder(
            context.contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        )
            .setContentValues(contentValues)
            .build()

        val pendingRecording = videoCapture.output
            .prepareRecording(context, outputOptions)

        if (withAudio && PermissionChecker.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PermissionChecker.PERMISSION_GRANTED
        ) {
            pendingRecording.withAudioEnabled()
        }

        currentRecording = pendingRecording.start(
            androidx.core.content.ContextCompat.getMainExecutor(context)
        ) { event ->
            when (event) {
                is VideoRecordEvent.Start -> {
                    _recordingState.value = RecordingState.Recording
                }
                is VideoRecordEvent.Status -> {
                    _recordingDuration.value = event.recordingStats.recordedDurationNanos / 1_000_000
                }
                is VideoRecordEvent.Finalize -> {
                    val uri = event.outputResults.outputUri
                    if (event.hasError()) {
                        _recordingState.value = RecordingState.Error(
                            "Recording failed: ${event.cause?.message}"
                        )
                    } else {
                        // Mark as not pending
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val updateValues = ContentValues().apply {
                                put(MediaStore.Video.Media.IS_PENDING, 0)
                            }
                            context.contentResolver.update(uri, updateValues, null, null)
                        }
                        _recordingState.value = RecordingState.Completed(uri)
                    }
                    currentRecording = null
                }
            }
        }
    }

    /**
     * Stop current recording.
     */
    fun stopRecording() {
        currentRecording?.stop()
        _recordingState.value = RecordingState.Stopping
    }

    /**
     * Pause current recording.
     */
    fun pauseRecording() {
        currentRecording?.pause()
        _recordingState.value = RecordingState.Paused
    }

    /**
     * Resume paused recording.
     */
    fun resumeRecording() {
        currentRecording?.resume()
        _recordingState.value = RecordingState.Recording
    }

    fun resetState() {
        _recordingState.value = RecordingState.Idle
        _recordingDuration.value = 0L
    }
}

sealed class RecordingState {
    data object Idle : RecordingState()
    data object Recording : RecordingState()
    data object Paused : RecordingState()
    data object Stopping : RecordingState()
    data class Completed(val uri: Uri) : RecordingState()
    data class Error(val message: String) : RecordingState()
}
