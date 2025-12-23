# Proguard configuration for NoghreSod Android App

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Hilt
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**

# Retrofit
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep interface retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keep class com.google.gson.** { *; }

# Room
-keep class androidx.room.Room { *; }
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.**

# Firebase
-keep class com.google.firebase.** { *; }
-keepnames class com.firebase.** { *; }
-keepnames class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Domain Models
-keep class com.noghre.sod.domain.model.** { *; }
-keep class com.noghre.sod.domain.usecase.** { *; }
-keep class com.noghre.sod.data.** { *; }

# View Models
-keep class com.noghre.sod.presentation.viewmodel.** { *; }

# DTOs
-keep class com.noghre.sod.data.remote.dto.** { *; }
-keepclassmembers class com.noghre.sod.data.remote.dto.** { *; }

# Local Classes
-keep class com.noghre.sod.data.local.entity.** { *; }
-keep class com.noghre.sod.data.local.dao.** { *; }

# Enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Application classes that have overrides
-keep class * extends android.app.Activity
-keep class * extends android.app.Service
-keep class * extends android.app.BroadcastReceiver
-keep class * extends android.content.ContentProvider
-keep class * extends android.app.backup.BackupAgentHelper
-keep class * extends android.preference.Preference
-keep class * extends android.view.View

# Keep constructors
-keep public class com.noghre.sod.** {
    public <init>(...);
}

# Remove logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
