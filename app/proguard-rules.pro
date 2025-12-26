# ============================================
# 🔐 NoghreSod ProGuard Configuration
# ============================================
# Security-focused ProGuard rules for production builds
# Obfuscates business logic while protecting critical components

# ============================================
# 📱 Android Framework Rules
# ============================================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve native method names
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Preserve annotations
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

# ============================================
# 🔐 Security-Critical Classes
# ============================================
# Keep security-related classes unobfuscated
-keep class com.noghre.sod.security.** { *; }
-keep interface com.noghre.sod.security.** { *; }

# Keep authentication and payment classes
-keep class com.noghre.sod.domain.model.User { *; }
-keep class com.noghre.sod.domain.model.Order** { *; }
-keep class com.noghre.sod.domain.model.Payment** { *; }

# ============================================
# 📦 Data Transfer Objects (Keep for serialization)
# ============================================
-keep class com.noghre.sod.data.remote.dto.** { *; }
-keep interface com.noghre.sod.data.remote.dto.** { *; }

# Keep database entities
-keep class com.noghre.sod.data.local.entity.** { *; }
-keep interface com.noghre.sod.data.local.entity.** { *; }

# ============================================
# 🔒 Serialization Support
# ============================================
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============================================
# 💾 GSON Serialization
# ============================================
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ============================================
# 🔄 Retrofit & OkHttp
# ============================================
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-dontwarn retrofit2.**

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

-keepclasseswithmembers class * {
    @retrofit2.http.<annotations> <methods>;
}

# ============================================
# 🔐 Certificate Pinning
# ============================================
-keep class okhttp3.CertificatePinner { *; }
-keep class okhttp3.CertificatePinner$** { *; }
-keep class okhttp3.internal.tls.OkHostnameVerifier { *; }

# ============================================
# 🛡️ Encrypted Shared Preferences
# ============================================
-keep class androidx.security.crypto.** { *; }
-keep interface androidx.security.crypto.** { *; }
-dontwarn androidx.security.**

# ============================================
# 📱 Jetpack & AndroidX
# ============================================
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

# ============================================
# 🎨 Compose
# ============================================
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }

# ============================================
# 💉 Hilt Dependency Injection
# ============================================
-keep class dagger.** { *; }
-keep interface dagger.** { *; }
-keep class javax.inject.** { *; }
-keepclasseswithmembernames class * {
    @javax.inject.* <fields>;
    @javax.inject.* <methods>;
}
-keep class * extends dagger.internal.Factory
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter

# Hilt generated code
-keep,allowobfuscation class * implements dagger.internal.ComponentImpl
-keep class *_Factory { *; }
-keep class *_MembersInjector { *; }
-keep,allowobfuscation class * extends dagger.hilt.internal.GeneratedComponent
-keep,allowobfuscation class * extends dagger.hilt.internal.GeneratedComponentManager

# ============================================
# 🚫 Remove Logging in Production
# ============================================
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void check*(...);
}

# ============================================
# 📱 Firebase
# ============================================
-keep class com.google.firebase.** { *; }
-keep interface com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Firebase Analytics
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Firebase Crashlytics
-keepattributes SourceFile,LineNumberTable
-keep public class com.noghre.sod.** extends java.lang.Exception { *; }

# ============================================
# 🔑 Keep BuildConfig
# ============================================
-keep class com.noghre.sod.BuildConfig { *; }

# ============================================
# ✅ Enum Classes
# ============================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================================
# 🏛️ Obfuscate Business Logic
# ============================================
# Aggressively obfuscate repositories and ViewModels
-keep class com.noghre.sod.presentation.screen.** { *; }
-keep class com.noghre.sod.data.repository.**Impl { *; }

# ============================================
# 🔧 Configuration
# ============================================
# Optimization passes
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose

# Remove unused code
-dontshrink
-dontoptimize

# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
