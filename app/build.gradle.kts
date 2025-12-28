plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("kotlin-serialization")
}

import java.util.Properties
import java.io.FileInputStream

android {
    namespace = "com.noghre.sod"
    compileSdk = 34

    // Load secrets from local.properties
    val secretsFile = rootProject.file("local.properties")
    val secrets = Properties()
    if (secretsFile.exists()) {
        secrets.load(FileInputStream(secretsFile))
    }
    
    defaultConfig {
        applicationId = "com.noghre.sod"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // BuildConfig values from secrets
        buildConfigField("String", "API_BASE_URL", "\"https://api.noghresod.com/api/\"")
        buildConfigField("String", "APP_NAME", "\"نقره‌سود\"")
        
        // Zarinpal Payment Gateway - from local.properties
        val zarinpalMerchantId = secrets.getProperty("zarinpal.merchant.id", "SANDBOX_MERCHANT_ID")
        val zarinpalSandboxId = secrets.getProperty("zarinpal.sandbox.merchant.id", "SANDBOX_MERCHANT_ID")
        buildConfigField("String", "ZARINPAL_MERCHANT_ID", "\"$zarinpalMerchantId\"")
        buildConfigField("String", "ZARINPAL_SANDBOX_MERCHANT_ID", "\"$zarinpalSandboxId\"")
        
        // Firebase Configuration (if needed from properties)
        val firebaseProjectId = secrets.getProperty("firebase.project.id", "")
        if (firebaseProjectId.isNotEmpty()) {
            buildConfigField("String", "FIREBASE_PROJECT_ID", "\"$firebaseProjectId\"")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("Boolean", "DEBUG_LOGGING", "false")
            buildConfigField("Boolean", "ENABLE_CERTIFICATE_PINNING", "true")
            buildConfigField("Boolean", "ENABLE_CRASHLYTICS", "true")
        }
        debug {
            isMinifyEnabled = false
            buildConfigField("Boolean", "DEBUG_LOGGING", "true")
            buildConfigField("Boolean", "ENABLE_CERTIFICATE_PINNING", "false")
            buildConfigField("Boolean", "ENABLE_CRASHLYTICS", "false")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ============================================
    // Core Android (UPDATED)
    // ============================================
    implementation("androidx.core:core-ktx:1.15.0")              // 1.12.0 -> 1.15.0
    implementation("androidx.appcompat:appcompat:1.7.0")         // 1.6.1 -> 1.7.0
    implementation("androidx.activity:activity-compose:1.9.3")   // 1.8.1 -> 1.9.3
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7") // 2.7.0 -> 2.8.7

    // ============================================
    // Jetpack Compose (UPDATED)
    // ============================================
    val composeBom = platform("androidx.compose:compose-bom:2024.12.00") // 2024.01.00 -> 2024.12.00
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.1")           // 1.2.0 -> 1.3.1
    implementation("androidx.compose.material:material-icons-extended:1.7.6") // 1.6.2 -> 1.7.6

    // ============================================
    // Jetpack Libraries (UPDATED)
    // ============================================
    implementation("androidx.navigation:navigation-compose:2.8.5")  // 2.7.7 -> 2.8.5
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.datastore:datastore-preferences-core:1.1.1")
    implementation("androidx.work:work-runtime-ktx:2.10.0")        // 2.9.1 -> 2.10.0
    implementation("androidx.paging:paging-runtime:3.3.5")         // 3.2.1 -> 3.3.5
    implementation("androidx.paging:paging-compose:3.3.5")         // 3.2.1 -> 3.3.5

    // ============================================
    // Dependency Injection (Hilt) (UPDATED)
    // ============================================
    implementation("com.google.dagger:hilt-android:2.54")          // 2.50 -> 2.54
    kapt("com.google.dagger:hilt-compiler:2.54")                  // 2.50 -> 2.54

    // ============================================
    // Networking (UPDATED)
    // ============================================
    implementation("com.squareup.retrofit2:retrofit:2.11.0")       // 2.10.0 -> 2.11.0
    implementation("com.squareup.retrofit2:converter-gson:2.11.0") // 2.10.0 -> 2.11.0
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3") // 1.6.2 -> 1.7.3

    // ============================================
    // JSON & Serialization
    // ============================================
    implementation("com.google.code.gson:gson:2.10.1")

    // ============================================
    // Image Loading (UPDATED)
    // ============================================
    implementation("io.coil-kt:coil-compose:2.7.0")                // 2.5.0 -> 2.7.0

    // ============================================
    // Security & Encryption
    // ============================================
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // ============================================
    // Firebase (UPDATED)
    // ============================================
    implementation("com.google.firebase:firebase-bom:33.7.0")      // 32.7.0 -> 33.7.0
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-crashlytics")

    // ============================================
    // Google Play Services
    // ============================================
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")

    // ============================================
    // Logging & Analytics
    // ============================================
    implementation("com.jakewharton.timber:timber:5.0.1")

    // ============================================
    // Additional Libraries (NEW)
    // ============================================
    // Root detection for security
    implementation("com.scottyab:rootbeer-lib:0.1.0")
    
    // Compose-friendly permissions
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")
    
    // App startup optimization
    implementation("androidx.startup:startup-runtime:1.2.0")

    // ============================================
    // Testing - Unit Tests
    // ============================================
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("app.cash.turbine:turbine:1.1.0")
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("androidx.room:room-testing:2.6.1")

    // ============================================
    // Testing - UI/Android Tests
    // ============================================
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // ============================================
    // Debug Tools
    // ============================================
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14") // NEW - Memory leak detection
}

// Configure Hilt
kapt {
    correctErrorTypes = true
}
