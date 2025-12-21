# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-dontwarn retrofit2.**

# Okhttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Kotlin Serialization
-keepclassmembers class ** {
    *** Companion;
}
-keepclasseswithmembers class ** {
    kotlinx.serialization.json.JsonElement *;
}
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class *
-keep @dagger.hilt.android.AndroidEntryPoint class *

# Room
-keep class androidx.room.** { *; }
-keepattributes *Annotation*

# Datastore
-keep class androidx.datastore.** { *; }

# Your app's package
-keep class com.noghre.sod.** { *; }
