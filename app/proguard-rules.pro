# =================================
# NOGHRESOD PROGUARD RULES
# ===================================
# ProGuard rules for release optimization
# Handles: obfuscation, optimization, and shrinking

# ========== GENERAL ==========
# Keep source file names for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep annotations
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses,EnclosingMethod
-keepattributes MethodParameters

# ========== KOTLIN ==========
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**

-keepclassmembers class **$WhenMappings {
    <fields>;
}

-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Remove Kotlin intrinsics checks (only for debug)
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
    static void checkExpressionValueIsNotNull(java.lang.Object, java.lang.String);
    static void checkNotNullParameter(java.lang.Object, java.lang.String);
    static void checkNotNullExpressionValue(java.lang.Object, java.lang.String);
    static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String);
}

# ========== COROUTINES ==========
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ========== RETROFIT & OKHTTP ==========
-keepattributes Signature
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-dontwarn retrofit2.**

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# ========== GSON (JSON Serialization) ==========
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**

# Keep all DTO/Model classes with Gson annotations
-keep class com.noghre.sod.data.remote.dto.** { *; }
-keep class com.noghre.sod.domain.model.** { *; }

# Generic types
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ========== ROOM DATABASE ==========
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

-keep class com.noghre.sod.data.local.database.** { *; }
-keep class com.noghre.sod.data.local.dao.** { *; }
-keep class com.noghre.sod.data.local.entity.** { *; }
-dontwarn androidx.room.paging.**

# ========== HILT / DAGGER ==========
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }

-keep class * extends dagger.hilt.android.internal.managers.ApplicationComponentManager
-keep class **_HiltComponents$* { *; }
-keep class **Hilt** { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }
-keep class **_Impl { *; }

-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# ========== COMPOSE ==========
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

-keep @androidx.compose.runtime.Composable class ** { *; }
-keep @androidx.compose.runtime.Composable interface ** { *; }

# ========== COIL IMAGE LOADING ==========
-keep class coil.** { *; }
-keep interface coil.** { *; }
-dontwarn coil.**

# ========== DATASTORE ==========
-keep class androidx.datastore.*.** { *; }

# ========== PAGING ==========
-keep class androidx.paging.** { *; }
-dontwarn androidx.paging.**

# ========== FIREBASE ==========
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

-keep class com.google.firebase.analytics.** { *; }
-keep class com.google.firebase.crashlytics.** { *; }
-keep class com.google.firebase.messaging.** { *; }

# ========== APP SPECIFIC CLASSES ==========
# Keep all ViewModels
-keep class com.noghre.sod.presentation.viewmodel.** { *; }

# Keep Application class
-keep class com.noghre.sod.NoghreSodApp { *; }

# Keep MainActivity and Activities
-keep class com.noghre.sod.MainActivity { *; }
-keep class com.noghre.sod.presentation.ui.** { *; }

# Keep Services
-keep class com.noghre.sod.service.** { *; }

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ========== R CLASS ==========
# Keep R classes (resources)
-keepclassmembers class **.R$* {
    public static <fields>;
}

# ========== LOGGING REMOVAL ==========
# Remove Log.d, Log.v, Log.i calls in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** println(...);
}

# ========== OPTIMIZATION ==========
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-verbose

# Preserve line numbers for debugging
-keepparameternames

# ========== DEBUGGING ==========
# Uncomment during troubleshooting:
# -printconfiguration build/proguard-config.txt
# -printusage build/proguard-unused.txt
# -printmapping build/proguard-mapping.txt
