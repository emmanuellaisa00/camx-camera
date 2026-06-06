package com.beadrop.camera

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BeadropApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide components
        // All initialization is lazy via Hilt — no heavy work here
        // This ensures < 300ms cold start
    }
}
