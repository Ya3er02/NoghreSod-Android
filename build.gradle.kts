// Root project build.gradle.kts

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}

// ✅ DEPENDENCY RESOLUTION STRATEGY (ISSUE #10)
// Ensures all modules use consistent dependency versions
subprojects {
    configurations.all {
        resolutionStrategy {
            // Force consistent Kotlin versions across all modules
            force("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.get()}")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${libs.versions.kotlin.get()}")
            force("org.jetbrains.kotlin:kotlin-reflect:${libs.versions.kotlin.get()}")
            
            // Force consistent AndroidX Core
            force("androidx.core:core-ktx:${libs.versions.androidxCore.get()}")
            
            // Force consistent Compose (handled by BOM, but ensure it's enforced)
            force("androidx.compose:compose-bom:${libs.versions.composeBom.get()}")
            
            // Cache strategies
            cacheDynamicVersionsFor(10, "minutes")
            cacheChangingModulesFor(0, "seconds")
            
            // Prefer project modules over external
            preferProjectModules()
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
