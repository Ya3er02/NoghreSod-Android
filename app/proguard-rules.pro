# NoghreSod ProGuard Rules
# =========================

# Native Key Manager - Keep NDK methods
-keep class com.noghre.sod.core.security.NativeKeyManager {
    public java.lang.String getMerchantId();
    public java.lang.String getApiKey();
    public com.noghre.sod.core.security.PaymentCredentials getPaymentGatewayCredentials();
    public boolean isLibraryAvailable();
}

# Payment Credentials Data Class
-keep class com.noghre.sod.core.security.PaymentCredentials {
    public java.lang.String getMerchantId();
    public java.lang.String getApiKey();
}

# Money Type Safety Classes
-keep class com.noghre.sod.domain.model.Toman {
    public long component1();
    public com.noghre.sod.domain.model.Rial toRial();
}

-keep class com.noghre.sod.domain.model.Rial {
    public long component1();
    public com.noghre.sod.domain.model.Toman toToman();
}

# Payment Rate Limiter
-keep class com.noghre.sod.core.security.PaymentRateLimiter {
    public boolean canAttempt(java.lang.String);
    public int getAttemptCount(java.lang.String);
    public void resetUser(java.lang.String);
    public void clearAll();
}

# Payment Verification Cache
-keep interface com.noghre.sod.domain.usecase.payment.PaymentVerificationCache {
    *;
}

-keep class com.noghre.sod.domain.usecase.payment.InMemoryPaymentVerificationCache {
    *;
}

-keep class com.noghre.sod.domain.usecase.payment.VerificationRecord {
    *;
}

# Preserve Hilt Generated Classes
-keep class dagger.hilt.** { *; }
-keep class com.noghre.sod.** { *; }
-keep class hilt_aggregated_deps.** { *; }

# Preserve @Inject annotated classes
-keep class * {
    @javax.inject.Inject <init>(...);
}

# Preserve @Provides methods
-keep class * {
    @dagger.Provides *;
}

# Retrofit API Interfaces
-keep interface com.noghre.sod.data.remote.** { *; }
-keep class com.noghre.sod.data.remote.** { *; }

# Keep data models for serialization
-keep class com.noghre.sod.data.remote.dto.** { *; }
-keep class com.noghre.sod.domain.model.** { *; }

# Room Database
-keep class com.noghre.sod.data.local.** { *; }
-keep class com.noghre.sod.data.local.dao.** { *; }
-keep class com.noghre.sod.data.local.entity.** { *; }

# Preserve JNI Methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Suppress warnings for external libraries
-dontwarn java.lang.invoke.**
-dontwarn sun.misc.**
-dontwarn javax.annotation.**

# Aggressive obfuscation for security
-repackageclasses 'com.ns'
-allowaccessmodification

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable implementations
-keepclassmembers class * implements java.io.Serializable {
    private static final long serialVersionUID;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
}
