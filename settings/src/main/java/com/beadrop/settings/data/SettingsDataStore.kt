package com.beadrop.settings.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "beadrop_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataStore = context.settingsDataStore

    companion object {
        val GRID_ENABLED = booleanPreferencesKey("grid_enabled")
        val GRID_TYPE = stringPreferencesKey("grid_type")
        val LEVEL_ENABLED = booleanPreferencesKey("level_enabled")
        val SHUTTER_SOUND = booleanPreferencesKey("shutter_sound")
        val MIRROR_FRONT = booleanPreferencesKey("mirror_front")
        val GEO_TAGGING = booleanPreferencesKey("geo_tagging")
        val QUALITY_MODE = stringPreferencesKey("quality_mode")
        val VIDEO_RESOLUTION = stringPreferencesKey("video_resolution")
        val VIDEO_FRAME_RATE = stringPreferencesKey("video_frame_rate")
        val VIDEO_STABILIZATION = booleanPreferencesKey("video_stabilization")
        val HISTOGRAM_ENABLED = booleanPreferencesKey("histogram_enabled")
        val FOCUS_PEAKING_ENABLED = booleanPreferencesKey("focus_peaking_enabled")
        val ZEBRA_STRIPES_ENABLED = booleanPreferencesKey("zebra_stripes_enabled")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val HIGH_CONTRAST_MODE = booleanPreferencesKey("high_contrast_mode")
        val SAVE_LOCATION = stringPreferencesKey("save_location")
        val WATERMARK_ENABLED = booleanPreferencesKey("watermark_enabled")
    }

    fun <T> getSetting(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    suspend fun <T> setSetting(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    val shutterSoundEnabled: Flow<Boolean> = getSetting(SHUTTER_SOUND, true)
    val mirrorFrontCamera: Flow<Boolean> = getSetting(MIRROR_FRONT, true)
    val geoTaggingEnabled: Flow<Boolean> = getSetting(GEO_TAGGING, false)
    val hapticFeedbackEnabled: Flow<Boolean> = getSetting(HAPTIC_FEEDBACK, true)
    val highContrastMode: Flow<Boolean> = getSetting(HIGH_CONTRAST_MODE, false)
}
