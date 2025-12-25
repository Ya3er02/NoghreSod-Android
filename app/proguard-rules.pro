# ============================================
# 🛡️ ProGuard Configuration for NoghreSod
# ============================================

# Keep main application classes
-keep class com.noghre.sod.MainActivity { *; }
-keep class com.noghre.sod.** { *; }

# ============================================
# 📡 Retrofit Configuration
# ============================================
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retrofit interfaces
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Retrofit classes
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# ============================================
# 📦 OkHttp Configuration
# ============================================
-keep class okhttp3.internal.** { *; }
-dontwarn okhttp3.internal.**

# ============================================
# 📚 Gson Configuration
# ============================================
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# DTO classes - keep all fields for Gson
-keep class com.noghre.sod.data.remote.dto.** { *; }
-keep class com.noghre.sod.data.local.entity.** { *; }

# ============================================
# 🗄️ Room Database Configuration
# ============================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-dontwarn androidx.room.paging.**

# ============================================
# 💉 Hilt Dependency Injection
# ============================================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class *

# ============================================
# 🏗️ Android Lifecycle
# ============================================
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# Keep all activities, services, broadcast receivers, etc
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep view constructors for inflation
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# ============================================
# 🔐 Kotlin Coroutines
# ============================================
-keepclassmembernames class kotlinx.coroutines.internal.MainDispatcherFactory {
    *** create(...);
}
-keepclassmembernames class kotlinx.coroutines.CoroutineExceptionHandler {
    *** handleException(...);
}
-keep class kotlin.coroutines.jvm.internal.** { *; }

# ============================================
# 📊 Jetpack Compose
# ============================================
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }

# ============================================
# 🎨 Material 3
# ============================================
-keep class com.google.android.material.** { *; }
-keep interface com.google.android.material.** { *; }

# ============================================
# 📸 Coil Image Loading
# ============================================
-keep class coil.** { *; }
-keep interface coil.** { *; }

# ============================================
# 🔒 Encryption Libraries
# ============================================
-keepclassmembers class * {
    *** encode(...);
    *** decode(...);
}

# ============================================
# 🚫 Warnings to Ignore
# ============================================
-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn com.google.android.material.**
-dontwarn androidx.**
-dontwarn org.jetbrains.kotlin.**

# ============================================
# 🎯 Optimization
# ============================================
-optimizationpasses 5
-allowaccessmodification
-mergeinterfacesaggressively

# ============================================
# 📝 Line Numbers for Crash Reports
# ============================================
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
