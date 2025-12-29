package com.noghre.sod.core.startup

import android.content.Context
import androidx.startup.Initializer
import com.noghre.sod.BuildConfig
import timber.log.Timber

/**
 * Initializer برای Timber logging library
 * 
 * این automatically به صورت eager-loaded اجرا می‌شود موقع app startup
 * 
 * AndroidManifest.xml را ببین برای <provider> configuration
 */
class TimberInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("✅ Timber initialized (Debug Mode)")
        } else {
            // TODO: Plant a release tree that sends logs to analytics service
            Timber.d("✅ Timber initialized (Release Mode)")
        }
    }
    
    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}
