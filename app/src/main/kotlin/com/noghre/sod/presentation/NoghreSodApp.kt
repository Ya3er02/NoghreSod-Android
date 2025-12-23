package com.noghre.sod.presentation

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NoghreSodApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize app dependencies here if needed
        // e.g., Analytics, Crash Reporting, etc.
    }
}
