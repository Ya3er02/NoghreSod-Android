# ProGuard rules for Noghresod Android App
# Comprehensive security and optimization rules

# ============== GENERAL RULES ==============
-verbose
-optimizationpasses 5
-mergeinterfacesaggressively

# ============== KEEP CORE CLASSES ==============
# Keep our custom application classes
-keep class com.noghre.sod.** { *; }
-keep class com.noghre.sod.presentation.** { *; }
-keep class com.noghre.sod.domain.** { *; }
-keep class com.noghre.sod.data.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom view constructors
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# ============== ANDROIDX & JETPACK ==============
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.room.** { *; }
-keep class androidx.navigation.** { *; }
-keep class androidx.hilt.** { *; }

# ============== DEPENDENCY INJECTION (HILT) ==============
-keep class dagger.hilt.** { *; }
-keep class dagger.hilt.android.** { *; }
-keep interface dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class *
-keep @dagger.hilt.android.AndroidEntryPoint class *
-keep @dagger.hilt.** class * { *; }
-keep @javax.inject.** class * { *; }

# ============== DATABASE (ROOM) ==============
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.DatabaseConfiguration { *; }
-dontwarn androidx.room.**

# ============== NETWORKING (RETROFIT + OKHTTP) ==============
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class com.google.gson.** { *; }
-keep interface com.google.gson.** { *; }
-keepclassmembers class * {
    @retrofit2.http.** <methods>;
}

# GSON Configuration
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { <fields>; }
-keep class * extends com.google.gson.TypeAdapter { *; }
-keep class * implements com.google.gson.JsonSerializable
-keep class * implements com.google.gson.JsonDeserializer

# Serialization
-keep class kotlinx.serialization.** { *; }
-keep class kotlin.serialization.** { *; }
-keepclassmembers class ** {
    *** *Field(...);
}

# ============== KOTLIN SPECIFIC ==============
-keep class kotlin.** { *; }
-keep class kotlin.jvm.internal.** { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-keep class kotlinx.coroutines.** { *; }
-keep interface kotlinx.coroutines.** { *; }
-keep class kotlinx.coroutines.flow.** { *; }

# ============== COMPOSE SPECIFIC ==============
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.animation.** { *; }

# ============== IMAGE LOADING (COIL) ==============
-keep class coil.** { *; }
-keep interface coil.** { *; }
-keep class io.coil_kt.coil.** { *; }

# ============== FIREBASE ==============
-keep class com.google.firebase.** { *; }
-keep interface com.google.firebase.** { *; }
-keep class com.google.android.firebase.** { *; }

# ============== SECURITY & ENCRYPTION ==============
-keep class androidx.security.crypto.** { *; }
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }

# ============== TIMBER (LOGGING) ==============
-keep class timber.log.** { *; }
-keep interface timber.log.** { *; }

# ============== ANNOTATIONS ==============
-keep @interface androidx.annotation.** { *; }
-keep @interface kotlin.annotation.** { *; }
-keep @interface javax.annotation.** { *; }
-keep @interface dagger.** { *; }

# ============== ENUM CLASSES ==============
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============== DATA CLASSES ==============
-keep class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(...);
    private void readObject(...);
    java.lang.Object writeReplace(...);
    java.lang.Object readResolve(...);
}

# ============== PARAMETER NAMES ==============
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keepattributes LocalVariableTable,LocalVariableTypeTable,MethodParameters

# ============== KOTLIN METADATA ==============
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepattributes InnerClasses
-keep class kotlin.Metadata {
    ***;
}

# ============== OPTIMIZATION ==============
-optimizations !code/allocation/variable
-optimizations code/simplification/arithmetic
-optimizations code/simplification/string
-optimizations method/marking/private
-optimizations method/inlining/short

# ============== REMOVALS ==============
-dontwarn com.google.android.**
-dontwarn android.support.**
-dontwarn androidx.legacy.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn com.squareup.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn com.sun.org.apache.**

# ============== REFLECTION PROTECTION ==============
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,Synthetic,EnclosingMethod,RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations,AnnotationDefault
