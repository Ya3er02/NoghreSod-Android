# ProGuard rules for NoghreSod Android Application

# Kotlin Coroutines
-keepnames class kotlin.coroutines.Continuation
-keep class kotlin.coroutines.** { *; }
-dontwarn kotlin.coroutines.**

# Kotlin Serialization
-keep class kotlin.serialization.** { *; }
-dontwarn kotlin.serialization.**

# Retrofit
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-dontwarn retrofit2.**
-dontnote retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn java.lang.invoke.*

# Gson
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.google.gson.**

# Room Database
-keep class androidx.room.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn androidx.room.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep interface dagger.hilt.** { *; }
-keep interface javax.inject.** { *; }
-dontwarn dagger.hilt.**
-dontwarn javax.inject.**

# Hilt Kapt
-keep class **_HiltModules { *; }
-keep class **_Factory { *; }
-keep class **_Impl { *; }
-keep class **_Provide { *; }

# Keep all enum values and valueOf methods
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep all serializable classes
-keep class * implements java.io.Serializable { *; }
-keepattributes EnclosingClass, InnerClasses
-keep class **.R
-keep class **.R$* {
    <fields>;
}

# Keep data models
-keep class com.noghre.sod.data.model.** { *; }
-keep class com.noghre.sod.data.dto.** { *; }

# Timber
-keep class timber.log.** { *; }

# AndroidX
-keep class androidx.** { *; }
-dontwarn androidx.**

# Kotlin
-keep class kotlin.** { *; }
-keepclassmembers class kotlin.** {
    *;
}

# Optimization
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose

# Verbose logging
-printmapping out/mapping.txt
-printseeds out/seeds.txt
-printusage out/usage.txt
