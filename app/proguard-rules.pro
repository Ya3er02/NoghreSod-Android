# Optimize ProGuard for release builds

# ============== General Rules ==============

# Keep line numbers for crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep all public interfaces
-keep public interface * { public *; }

# ============== Moshi Serialization ==============

# Keep all data classes and their fields for Moshi
-keep class com.noghre.sod.data.remote.dto.** { *; }
-keep class com.noghre.sod.domain.model.** { *; }

# Moshi annotations
-keepclassmembers class ** {
    @com.squareup.moshi.* <methods>;
}

# Keep constructors for Moshi generated adapters
-keepclasseswithmembernames class ** {
    @com.squareup.moshi.* <fields>;
}

# ============== Retrofit ==============

# Preserve Retrofit API interfaces
-keep interface com.noghre.sod.data.remote.api.** { *; }
-keep interface com.noghre.sod.data.remote.ApiService { *; }

# Retrofit annotations
-keepattributes Signature
-keepattributes InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations

# Keep Retrofit classes
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# ============== OkHttp ==============

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# OkHttp interceptors
-keep class com.noghre.sod.data.remote.interceptor.** { *; }
-keep class com.noghre.sod.data.remote.Interceptors** { *; }
-keep class com.noghre.sod.data.remote.AuthInterceptor { *; }

# ============== Room Database ==============

# Keep Room entities and DAOs
-keep class com.noghre.sod.data.local.db.** { *; }
-keep interface com.noghre.sod.data.local.db.** { *; }

# Room database class
-keep class com.noghre.sod.data.local.db.*_Impl { *; }
-keep class * extends androidx.room.RoomDatabase { *; }

# ============== Hilt Dependency Injection ==============

# Keep Hilt components
-keep class com.noghre.sod.di.** { *; }
-keep class dagger.hilt.** { *; }

# Keep Hilt generated classes
-keep class **_Factory { *; }
-keep class **_Impl { *; }
-keep class **_HiltModules { *; }

# ============== Domain Models & Business Logic ==============

# Keep domain use cases
-keep class com.noghre.sod.domain.usecase.** { *; }
-keep class com.noghre.sod.domain.repository.** { *; }
-keep class com.noghre.sod.domain.model.** { *; }

# Keep Result sealed class
-keep class com.noghre.sod.domain.model.Result { *; }
-keep class com.noghre.sod.domain.model.Result$* { *; }

# ============== Security & Encryption ==============

-keep class com.noghre.sod.data.remote.security.** { *; }
-keep class com.noghre.sod.data.local.security.** { *; }
-keep class com.noghre.sod.data.remote.CertificatePinningUtil { *; }

# ============== Exception Handling ==============

-keep class com.noghre.sod.domain.model.AppException { *; }
-keep class com.noghre.sod.domain.model.AppException$* { *; }
-keep class com.noghre.sod.data.remote.exception.** { *; }

# ============== Core & Utils ==============

-keep class com.noghre.sod.core.** { *; }
-keep class com.noghre.sod.utils.** { *; }

# ============== Firebase (if used) ==============

-keep class com.google.firebase.** { *; }
-keep interface com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# ============== Kotlin ==============

-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**

# Kotlin metadata
-keepattributes *Annotation*,Signature,Exception

# ============== AndroidX ==============

-keep class androidx.** { *; }
-dontwarn androidx.**

# ============== Compose ==============

-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ============== Gson (if used alongside Moshi) ==============

-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# ============== Third Party Libraries ==============

# Timber logging
-keep class timber.log.** { *; }

# Coil image loading
-keep class coil.** { *; }
-dontwarn coil.**

# PersianDate
-keep class com.zohoyn.persiandate.** { *; }
-dontwarn com.zohoyn.persiandate.**

# ============== Debug Build Support ==============

# Allow debugging in debug builds
-keepattributes LineNumberTable
-keepattributes SourceFile

# ============== Verbose Output ==============

# Uncomment for detailed ProGuard output
# -verbose
# -printseeds
# -printusage
