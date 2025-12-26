plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.detekt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.noghre.sod"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.noghre.sod"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        
        // ✅ BUILD CONFIG FIELDS (ISSUE #17)
        buildConfigField("String", "APP_VERSION", "\"${versionName}\"")
        buildConfigField("int", "VERSION_CODE", "${versionCode}")
    }
    
    // ✅ SECURE RELEASE SIGNING
    signingConfigs {
        // Debug signing (default)
        getByName("debug") {
            storeFile = file("${System.getProperty("user.home")}/.android/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        
        // ✅ Release signing from environment variables with fallback
        create("release") {
            val keystorePath = System.getenv("RELEASE_KEYSTORE_PATH")
            val keystorePassword = System.getenv("KEYSTORE_PASSWORD")
            val keyAlias = System.getenv("KEY_ALIAS")
            val keyPassword = System.getenv("KEY_PASSWORD")
            
            if (keystorePath != null && File(keystorePath).exists()) {
                // Production release - use provided keystore
                storeFile = file(keystorePath)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            } else {
                // Development fallback - use debug keystore
                logger.warn("⚠️ Release keystore not found. Using debug keystore for development builds.")
                storeFile = file("${System.getProperty("user.home")}/.android/debug.keystore")
                storePassword = "android"
                this.keyAlias = "androiddebugkey"
                this.keyPassword = "android"
            }
            
            // ✅ APK v2+ signing (more secure)
            enableV1Signing = false
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
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
            signingConfig = signingConfigs.getByName("release")
            
            // Production optimizations
            isDebuggable = false
            isJniDebuggable = false
            
            // Crash reporting
            buildConfigField("boolean", "CRASH_REPORTING_ENABLED", "true")
            buildConfigField("String", "API_BASE_URL", "\"https://api.noghresod.ir/v1/\"")
            buildConfigField("boolean", "DEBUG_LOGGING", "false")
        }
        
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            isJniDebuggable = true
            
            buildConfigField("boolean", "CRASH_REPORTING_ENABLED", "false")
            buildConfigField("String", "API_BASE_URL", "\"https://dev-api.noghresod.ir/v1/\"")
            buildConfigField("boolean", "DEBUG_LOGGING", "true")
        }
    }

    // ✅ BUILD FLAVORS FOR ENVIRONMENT MANAGEMENT
    flavorDimensions += listOf("environment")
    
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "API_BASE_URL", "\"https://dev-api.noghresod.ir/v1/\"")
            buildConfigField("boolean", "CERTIFICATE_PINNING_ENABLED", "false")
        }
        
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            buildConfigField("String", "API_BASE_URL", "\"https://staging-api.noghresod.ir/v1/\"")
            buildConfigField("boolean", "CERTIFICATE_PINNING_ENABLED", "true")
        }
        
        create("prod") {
            dimension = "environment"
            buildConfigField("String", "API_BASE_URL", "\"https://api.noghresod.ir/v1/\"")
            buildConfigField("boolean", "CERTIFICATE_PINNING_ENABLED", "true")
        }
    }
    
    // Filter variants (dev only for debug, prod for release)
    variantFilter {
        if (buildType.name == "release" && flavors.any { it.name == "dev" }) {
            ignore = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // ✅ ENABLE CORE LIBRARY DESUGARING FOR JAVA TIME API (ISSUE #46)
        isCoreLibraryDesugaringEnabled = true
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    
    lint {
        checkReleaseBuilds = true
        abortOnError = false
        disable += "MissingTranslation"
    }
}

// ✅ DEPENDENCY RESOLUTION STRATEGY
subprojects {
    configurations.all {
        resolutionStrategy {
            // Force consistent Kotlin version across all modules
            force("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.get()}")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${libs.versions.kotlin.get()}")
            
            // Force consistent AndroidX versions
            force("androidx.core:core-ktx:${libs.versions.androidxCore.get()}")
            
            // Prefer project modules
            preferProjectModules()
        }
    }
}

dependencies {
    // Android Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt - Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp3.logging.interceptor)
    implementation(libs.okhttp)

    // Database
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)

    // Paging
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    // Image Loading
    implementation(libs.coil.compose)

    // Security
    implementation(libs.androidx.security.crypto)

    // Serialization
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization.json)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // ViewModel & LiveData
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    
    // WorkManager for background tasks
    implementation(libs.androidx.work.runtime.ktx)
    
    // DataStore (modern SharedPreferences)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)
    
    // ✅ TIMBER LOGGING (ISSUE #16)
    implementation(libs.timber)

    // ✅ FIREBASE
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.config.ktx)

    // ✅ CORE LIBRARY DESUGARING FOR JAVA TIME API (ISSUE #46)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.3")

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.compose.ui.test.manifest)

    // Debugging
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// ✅ BUILD CONFIGURATION
configurations.all {
    resolutionStrategy {
        // Force specific versions to prevent conflicts
        force("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.get()}")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${libs.versions.kotlin.get()}")
        
        // Cache dynamic versions
        cacheDynamicVersionsFor(10, "m")
        cacheChangingModulesFor(0, "s")
        
        // Prefer project modules
        preferProjectModules()
    }
}

// ✅ DETEKT CONFIGURATION
detekt {
    config.setFrom(files("detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
}
