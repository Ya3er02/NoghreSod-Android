# ProGuard Rules - Comprehensive & Secure Configuration
# Last updated: 2025-12-25
# Level: Enterprise Security

# ==========================
# 1. AGGRESSIVE OBFUSCATION
# ==========================
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
-verbose

# Obfuscation settings
-obfuscationdictionary obfuscation_dictionary.txt
-packageobfuscationdictionary package_dictionary.txt
-classObfuscationDictionary class_dictionary.txt

# Aggressive optimization
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*,!code/allocation/variable

# ==========================
# 2. KEEP ONLY ESSENTIAL CLASSES
# ==========================

# Entry Points
-keep public class com.noghre.sod.NoghreSodApp extends android.app.Application
-keep public class com.noghre.sod.presentation.MainActivity extends android.app.Activity

# Android Components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference

# Keep Fragments (required for reflection)
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends android.app.Fragment

# ==========================
# 3. ARCHITECTURE COMPONENTS
# ==========================

# ViewModels (Hilt creates them via reflection)
-keep public class * extends androidx.lifecycle.ViewModel
-keepclasseswithmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep UseCase classes
-keep public class com.noghre.sod.domain.usecase.** { *; }
-keepclasseswithmembers class com.noghre.sod.domain.usecase.** {
    public <init>(...);
}

# Repositories - OBFUSCATE (not keep all)
-keep public class * extends com.noghre.sod.domain.repository.** { *; }
-keep public class com.noghre.sod.data.repository.** { *; }

# ==========================
# 4. DATA MODELS (for serialization)
# ==========================

# DTOs - keep fields for JSON mapping
-keep class com.noghre.sod.data.remote.dto.** {
    <fields>;
}
-keepclasseswithmembers class com.noghre.sod.data.remote.dto.** {
    <init>(...);
}

# Domain Models - keep fields
-keep class com.noghre.sod.domain.model.** {
    <fields>;
}
-keepclasseswithmembers class com.noghre.sod.domain.model.** {
    <init>(...);
}

# ==========================
# 5. DATABASE ENTITIES
# ==========================

# Room entities
-keep @androidx.room.Entity class * {
    <init>(...);
    <fields>;
}

# Room DAOs
-keep @androidx.room.Dao interface * {
    <methods>;
}

# Room database
-keep @androidx.room.Database class * {
    <init>(...);
}

# ==========================
# 6. DEPENDENCY INJECTION (Hilt)
# ==========================

# Hilt generated code
-keep class hilt_aggregated_deps.** { *; }
-keep class **_HiltModules.** { *; }
-keep class **_Factory { *; }
-keep class **_Provide* { *; }

# Hilt Annotations
-keep @dagger.hilt.android.HiltAndroidApp class *
-keep @dagger.hilt.android.AndroidEntryPoint class *
-keep @dagger.Module class * {
    <methods>;
}
-keep @dagger.Provides class * {
    <methods>;
}

# ==========================
# 7. SERIALIZATION & JSON
# ==========================

# Keep Gson-related classes if used
-keep class com.google.gson.** { *; }
-keep class **.GsonFactory { *; }
-keepclasseswithmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Kotlin Serialization
-keep class kotlinx.serialization.json.** { *; }
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <methods>;
}

# ==========================
# 8. RETROFIT & NETWORK
# ==========================

# Retrofit
-keep interface * {
    @retrofit2.** <methods>;
}
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Keep API interfaces
-keep public interface com.noghre.sod.data.remote.api.** { *; }

# ==========================
# 9. JETPACK COMPOSE
# ==========================

# Compose runtime
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material3.** { *; }

# ==========================
# 10. REMOVE DEBUG INFO
# ==========================

# Remove logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Remove source file and line numbers
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# ==========================
# 11. REMOVE UNUSED CODE
# ==========================

# Remove unused attributes
-dontwarn android.**
-dontwarn androidx.**
-dontwarn com.google.**
-dontwarn retrofit2.**
-dontwarn okhttp3.**

# Remove unused interfaces implementations
-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

# ==========================
# 12. KEEP NATIVE METHODS
# ==========================

# Keep native methods for JNI (NDK keys)
-keepclasseswithmembernames class * {
    native <methods>;
}

# ==========================
# 13. VERIFICATION
# ==========================

# Verify obfuscation
-printmapping build/outputs/mapping/release/mapping.txt
-printusage build/outputs/usage/release/usage.txt
-printseeds build/outputs/seeds/release/seeds.txt

# ==========================
# FINAL SECURITY CHECK
# ==========================

# Ensure NO classes are kept as-is
# All business logic MUST be obfuscated
# Only keep what's absolutely necessary for Android framework
