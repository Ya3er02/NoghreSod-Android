# NoghreSod ProGuard Rules
# Minification and obfuscation rules for release build

# Keep all public and protected classes and methods
-keep public class *
-keep protected class *

# ===== Retrofit Rules =====
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep Retrofit service interfaces
-keep class * extends retrofit2.http.Interceptor
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# ===== Gson Rules =====
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Keep all data classes from DTOs
-keep class com.noghre.sod.data.remote.dto.** { *; }
-keep class com.noghre.sod.data.local.entity.** { *; }
-keep class com.noghre.sod.domain.model.** { *; }

# Preserve generic type info
-keepclassmembers class * {
    <fields>;
}

# ===== Room Database Rules =====
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.paging.**

# Keep Room entity fields
-keepclassmembers @androidx.room.Entity class * {
    @androidx.room.ColumnInfo <init>(...);
    public <init>(...);
}

# ===== Hilt/Dagger Rules =====
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.* class * { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier
-keep class * extends dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories.InternalFactoryFactory
-keepnames @dagger.hilt.android.HiltAndroidApp class *

# ===== Kotlin Coroutines Rules =====
-keep class kotlin.coroutines.** { *; }
-keepnames class kotlinx.coroutines.** { *; }
-keepclassmembernames class kotlinx.coroutines.** {
    volatile <fields>;
}

# ===== Android Framework Rules =====
-keep public class android.** { public protected *; }
-keep class androidx.** { *; }
-keep class com.google.android.** { *; }

# ===== Jetpack Compose Rules =====
-keep class androidx.compose.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.runtime.** { *; }

# ===== Firebase Rules =====
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ===== OkHttp Rules =====
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ===== General Rules =====
# Keep class names for reflection
-keepnames class * implements android.os.Parcelable { *; }
-keepnames class * implements java.io.Serializable { *; }

# Keep native method names
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep View subclasses for inflation
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Keep Activity, Fragment, Service, etc.
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep Application subclass
-keep public class * extends android.app.Application

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ===== Custom Application Rules =====
# Keep NativeKeys for JNI
-keep class com.noghre.sod.core.security.NativeKeys { *; }

# Keep RootDetector
-keep class com.noghre.sod.core.security.RootDetector { *; }

# Keep security-related classes
-keep class com.noghre.sod.core.security.** { *; }

# Keep network configuration
-keep class com.noghre.sod.core.network.** { *; }

# ===== Verbose Output =====
-verbose
-keepdirectories libs