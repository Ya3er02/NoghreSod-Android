package com.noghre.sod

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Custom test runner برای proper Hilt initialization
 * 
 * Add این را به build.gradle.kts اضافه کنید:
 * 
 * android {
 *     testInstrumentationRunner = "com.noghre.sod.HiltTestRunner"
 * }
 */
class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader,
        className: String,
        context: Context
    ): Application {
        return super.newApplication(
            cl,
            HiltTestApplication::class.java.name,
            context
        )
    }
}
