# ProGuard/R8 configuration for NoghreSod Android App
# Ensure sensitive code and data structures are protected

# ============== General Rules ==============
-verbose
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

# ============== Kotlin Rules ==============
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.jvm.internal.Reflection

# ============== Android Framework ==============
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Fragment
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

# ============== Native Methods ==============
-keepclasseswithmembernames class * {
    native <methods>;
}

# ============== Animation ==============
-keep public class * extends android.view.animation.Animation {
    <fields>;
    <methods>;
}

# ============== View Constructors ==============
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# ============== GSON / JSON Serialization ==============
-keep class com.noghre.sod.data.remote.dto.** { *; }
-keep class com.noghre.sod.data.local.entity.** { *; }
-keep class com.noghre.sod.domain.model.** { *; }

# GSON field annotations
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Gson specific rules
-keep class com.google.gson.stream.** { *; }
-dontwarn sun.misc.Unsafe
-dontwarn com.google.gson.internal.UnsafeAllocator

# ============== Retrofit ==============
-keep class retrofit2.** { *; }
-keepclasseswithmembers class retrofit2.** {
    *;
}
-keepattributes Exceptions
-keepattributes InnerClasses

# ============== OkHttp ==============
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# ============== Room Database ==============
-keep class androidx.room.Room { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-dontwarn androidx.room.RoomOpenHelper

# ============== Hilt Dependency Injection ==============
-keep class * extends dagger.hilt.android.HiltAndroidApp
-keep @dagger.hilt.** class *
-keepclasseswithmembernames class * {
    @dagger.hilt.** *;
}

# ============== Jetpack Compose ==============
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }
-dontwarn androidx.compose.material3.tokens.**

# ============== Firebase ==============
-keep class com.firebase.** { *; }
-keep class com.google.firebase.** { *; }
-keepclassmembers class ** { @com.google.firebase.database.Exclude <fields>; }
-keepclassmembers class ** { @com.google.firebase.database.IgnoredOnParcel <fields>; }

# ============== Timber Logging ==============
-keep class timber.log.Timber
-keep class timber.log.Timber$* { *; }

# ============== Security ==============
# Keep encryption-related classes
-keep class androidx.security.crypto.** { *; }
-keep class com.noghre.sod.data.local.security.** { *; }

# ============== Build Config ==============
-keep class com.noghre.sod.BuildConfig { *; }

# ============== Reflection ==============
-keep class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============== Debugging ==============
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ============== Custom Classes ==============
# Keep exception classes for proper error handling
-keep class com.noghre.sod.domain.model.AppException { *; }
-keep class com.noghre.sod.domain.model.Result { *; }

# Keep network-related classes
-keep class com.noghre.sod.data.remote.** { *; }
-keep class com.noghre.sod.data.remote.interceptor.** { *; }
Keep lifecycle classes
-keep class androidx.lifecycle.** { *; }
-keepclassmembers class * implements androidx.lifecycle.LifecycleObserver {
    <init>(...);
}

# ============== ViewModel ==============
-keep class androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ============== Kotlin Data Classes ==============
-keep class * implements kotlin.Cloneable {
    synthetic <init>(...);  
}

# ============== Enum Classes ==============
-keepclassmembers class * {
    **[] $VALUES;
    public *[] values();
    public * valueOf(java.lang.String);
}

# ============== Remove Logging in Release ==============
-assumenosideeffects class timber.log.Timber {
    public *** d(...);
    public *** v(...);
    public *** i(...);
}
