# ============== ProGuard Configuration for NoghreSod App ==============
# This configuration balances security, performance, and functionality.
# Generated code is obfuscated to prevent reverse engineering.
#
# Key principles:
# - Keep entry points (Activities, Services, Receivers, Providers)
# - Keep interfaces and callbacks
# - Aggressively shrink and obfuscate
# - Remove logging in release builds
#
# Documentation: https://www.guardsquare.com/proguard

# ============== General Settings ==============

# Optimization pass count (default: 1)
-optimizationpasses 5

# Verbose logging
-verbose

# Print mapping file
-printmapping build/outputs/mapping/release/mapping.txt

# Print seeds
-printseeds build/outputs/mapping/release/seeds.txt

# Print used members
-printusage build/outputs/mapping/release/unused.txt

# Optimizations
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-allowaccessmodification
-renameSourceFileAttribute SourceFile
-keepattributes SourceFile,LineNumberTable

# ============== Android Framework ==============

# Keep all Android framework classes
-keep public class android.** { *; }
-keep public class android.app.** { *; }
-keep public class android.content.** { *; }
-keep public class android.net.** { *; }
-keep public class android.os.** { *; }
-keep public class android.util.** { *; }
-keep public class android.view.** { *; }
-keep public class android.widget.** { *; }

# Keep AndroidX libraries
-keep public class androidx.** { *; }
-keep public class com.google.android.material.** { *; }

# ============== NoghreSod Application ==============

# Keep all application classes
-keep class com.noghre.sod.** { *; }
-keepclasseswithmembernames class com.noghre.sod.** { *; }
-keepclasseswithmembers class com.noghre.sod.** {
    *** *(...)
}

# Keep all model/data classes (these are often used via reflection)
-keep class com.noghre.sod.domain.model.** { *; }
-keep class com.noghre.sod.data.local.entity.** { *; }
-keep class com.noghre.sod.data.remote.dto.** { *; }

# Keep all Activities, Services, Receivers, Content Providers
-keep public class com.noghre.sod.presentation.** extends android.app.Activity
-keep public class com.noghre.sod.presentation.** extends android.app.Service
-keep public class com.noghre.sod.presentation.** extends android.content.BroadcastReceiver
-keep public class com.noghre.sod.presentation.** extends android.content.ContentProvider
-keep public class com.noghre.sod.presentation.** extends android.app.Fragment
-keep public class com.noghre.sod.presentation.** extends androidx.fragment.app.Fragment

# Keep Application class
-keep class com.noghre.sod.NoghreSodApp { *; }

# ============== Serialization Classes ==============

# Keep serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep Parcelable classes
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ============== Reflection & Annotations ==============

# Keep annotation classes
-keepattributes *Annotation*
-keep @interface com.noghre.sod.** { *; }
-keep class * extends java.lang.annotation.Annotation { *; }

# Keep classes with annotations
-keepclasseswithclassenannotation com.noghre.sod.** { *; }

# ============== Kotlin ==============

# Keep Kotlin metadata
-keepattributes SourceFile,LineNumberTable,*Annotation*,EnclosingMethod,Deprecated,InnerClasses,EnclosingClass
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-keep class kotlin.jvm.internal.** { *; }
-keep class kotlin.internal.** { *; }

# Keep Kotlin extension functions
-keep class kotlin.** { *; }
-keep interface kotlin.** { *; }
-dontwarn kotlin.**

# ============== Retrofit ==============

-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn retrofit2.**

# Keep Retrofit API interfaces
-keep public interface com.noghre.sod.data.remote.api.** { *; }

# ============== OkHttp ==============

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# ============== GSON ==============

-keep class com.google.gson.** { *; }
-keep interface com.google.gson.** { *; }
-keepattributes Signature

# Keep all classes used by GSON
-keep class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName *;
}

# ============== Room Database ==============

-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.**

# Keep all Room entities and DAOs
-keep class com.noghre.sod.data.local.entity.** { *; }
-keep class com.noghre.sod.data.local.dao.** { *; }
-keep public class com.noghre.sod.data.local.database.AppDatabase { *; }

# ============== Hilt Dependency Injection ==============

-keep class dagger.hilt.** { *; }
-keep class hilt_aggregated_deps.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.android.EarlyEntryPoint interface * { *; }
-keep @dagger.Module class * { *; }
-keep @dagger.Provides class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-dontwarn dagger.hilt.**

# ============== Firebase ==============

-keep class com.firebase.** { *; }
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.firebase.**
-dontwarn com.google.firebase.**

# ============== Timber Logging ==============

-keep class timber.log.** { *; }
# Remove Timber debug calls in release builds
-assumenosideeffects class timber.log.Timber {
    public static void d(...);
    public static void v(...);
}

# ============== Coroutines ==============

-keep class kotlinx.coroutines.** { *; }
-keep class kotlin.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# ============== Data Classes ==============

# Keep data class constructors and members
-keepclassmembers class * {
    *** component*(...);
    *** copy(...);
    *** copy$default(...);
}

# ============== Lambda Expressions ==============

-keepclassmembers class * {
    *** lambda*(...)
}

# ============== Native Methods ==============

-keepclasseswithmembernames class * {
    native <methods>;
}

# ============== Enum Classes ==============

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    int $ordinal;
    int[] $VALUES;
}

# ============== Resource Classes ==============

-keepclassmembers class **.R$* {
    public static <fields>;
}

# ============== View Constructors ==============

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
    *** get*(...);
}

# ============== Callback Interfaces ==============

-keep class * implements android.view.View$OnClickListener { *; }
-keep class * implements android.view.View$OnLongClickListener { *; }
-keep class * implements android.content.DialogInterface$OnClickListener { *; }

# ============== Warnings Suppression ==============

# Suppress warnings that are not important
-dontwarn java.lang.invoke.**
-dontwarn java.awt.**
-dontwarn javax.swing.**
-dontwarn sun.reflect.**
-dontwarn org.w3c.dom.**
-dontwarn org.xml.sax.**

# ============== Specific Libraries ==============

# Picasso/Glide image loading
-keep class com.bumptech.glide.** { *; }
-keep class com.bumptech.glide.load.** { *; }
-dontwarn com.bumptech.glide.**

# Moshi JSON
-keep class com.squareup.moshi.** { *; }
-keep class * extends com.squareup.moshi.JsonAdapter { *; }
-dontwarn com.squareup.moshi.**

# ============== Debug Symbols ==============

# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renameSourceFileAttribute SourceFile
