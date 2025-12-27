# ==============================================================================
# PROGUARD RULES FOR NOGHRESOD ANDROID APP
# ==============================================================================
# This file contains ProGuard/R8 rules to optimize and obfuscate the production
# build while maintaining functionality and debugging capabilities.
# ==============================================================================

# Keep line numbers for crash reporting (Crashlytics)
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep all annotations
-keepattributes *Annotation*

# Keep signatures
-keepattributes Signature

# Keep value classes (Kotlin)
-keepattributes InnerClasses

# Keep enclosing method
-keepattributes EnclosingMethod

# ============================================================
# 1. KOTLIN SPECIFIC RULES
# ============================================================

# Keep Kotlin classes
-keep class kotlin.** { *; }
-keep interface kotlin.** { *; }

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Keep Kotlin serialization
-keepattributes *Annotation*
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** {
    *** *Companion;
}

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# ============================================================
# 2. COMPOSE SPECIFIC RULES
# ============================================================

# Keep Compose runtime
-keep class androidx.compose.runtime.** { *; }
-keep interface androidx.compose.runtime.** { *; }

# Keep Compose foundation
-keep class androidx.compose.foundation.** { *; }
-keep interface androidx.compose.foundation.** { *; }

# Keep Compose material
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.material.** { *; }

# Keep Compose ui
-keep class androidx.compose.ui.** { *; }

# ============================================================
# 3. JETPACK LIBRARIES
# ============================================================

# AndroidX Libraries
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

# Room Database
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep class * extends androidx.room.RoomDatabase { *; }

# Lifecycle
-keep class androidx.lifecycle.** { *; }
-keep interface androidx.lifecycle.** { *; }

# Navigation
-keep class androidx.navigation.** { *; }

# DataStore
-keep class androidx.datastore.** { *; }

# Paging
-keep class androidx.paging.** { *; }

# ============================================================
# 4. DEPENDENCY INJECTION (HILT)
# ============================================================

# Hilt annotations
-keep @dagger.hilt.android.HiltAndroidApp class * {
    <init>();
}

-keep @dagger.hilt.android.AndroidEntryPoint class * {
    <init>();
}

-keep @dagger.hilt.android.lifecycle.HiltViewModel class * {
    <init>();
}

# Keep Hilt generated classes
-keep class **_HiltComponents { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }

# Dagger
-keep class dagger.** { *; }
-keep interface dagger.** { *; }
-dontwarn dagger.**

# ============================================================
# 5. RETROFIT & NETWORKING
# ============================================================

# Retrofit
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-dontwarn retrofit2.**

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn sun.misc.**

# Gson
-keep class com.google.gson.** { *; }
-keep interface com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Keep all model classes used with Retrofit/Gson
-keep class com.noghre.sod.data.remote.dto.** { *; }
-keep class com.noghre.sod.domain.model.** { *; }

# ============================================================
# 6. FIREBASE
# ============================================================

# Firebase
-keep class com.google.firebase.** { *; }
-keep interface com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Keep Firebase Analytics
-keep class com.google.android.gms.analytics.** { *; }

# Keep Google Play Services
-keep class com.google.android.gms.** { *; }
-keep interface com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# ============================================================
# 7. IMAGE LOADING (COIL)
# ============================================================

# Coil
-keep class coil.** { *; }
-keep interface coil.** { *; }
-dontwarn coil.**

# ============================================================
# 8. SECURITY
# ============================================================

# Keep security classes
-keep class androidx.security.** { *; }

# Root detection
-keep class com.scottyab.rootbeer.** { *; }

# ============================================================
# 9. APP SPECIFIC CLASSES
# ============================================================

# Keep all app's model/domain classes
-keep class com.noghre.sod.domain.model.** { *; }
-keep class com.noghre.sod.data.local.entity.** { *; }
-keep class com.noghre.sod.data.remote.dto.** { *; }

# Keep core classes
-keep class com.noghre.sod.core.error.** { *; }
-keep class com.noghre.sod.core.util.** { *; }
-keep class com.noghre.sod.core.network.** { *; }

# Keep DI modules
-keep class com.noghre.sod.di.** { *; }

# Keep ViewModels
-keep class com.noghre.sod.presentation.**.** extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep Composables (functions with @Composable)
-keep @androidx.compose.runtime.Composable public fun ** (...) 

# ============================================================
# 10. SERIALIZATION & REFLECTION
# ============================================================

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable classes
-keep class * implements android.os.Parcelable { *; }

# Keep classes with custom constructors for reflection
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep view constructors (for inflation)
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# ============================================================
# 11. LOGGING & DEBUGGING
# ============================================================

# Keep Timber logger
-keep class timber.log.** { *; }

# ============================================================
# 12. OPTIMIZATION
# ============================================================

# Allow optimization
-optimizationpasses 5
-dontoptimize  # Disabled for better compatibility, enable if needed

# Method inlining
-allowaccessmodification

# ============================================================
# 13. GENERAL RULES
# ============================================================

# Keep entry points
-keep public class * extends android.app.Activity { *; }
-keep public class * extends android.app.Service { *; }
-keep public class * extends android.app.BroadcastReceiver { *; }
-keep public class * extends android.content.ContentProvider { *; }
-keep public class * extends android.content.BroadcastReceiver { *; }
-keep public class * extends android.app.Fragment { *; }
-keep public class * extends androidx.fragment.app.Fragment { *; }

# Keep R classes
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Keep BuildConfig
-keep class com.noghre.sod.BuildConfig { *; }

# ============================================================
# 14. REMOVE LOGGING IN RELEASE
# ============================================================

# Remove all debug logging calls
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

# Remove Timber debug calls
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
}

# ============================================================
# 15. SUPPRESS WARNINGS
# ============================================================

-dontwarn java.lang.invoke.*
-dontwarn javax.naming.**
-dontwarn sun.reflect.**
-dontwarn java.beans.**
-dontwarn org.w3c.dom.bootstrap.**

# ============================================================
# 16. VERBOSE OUTPUT (OPTIONAL)
# ============================================================

# Uncomment for verbose output during ProGuard processing
# -verbose
# -printmapping proguard-mapping.txt
# -printconfiguration proguard-config.txt

# ==============================================================================
# END OF PROGUARD CONFIGURATION
# ==============================================================================
