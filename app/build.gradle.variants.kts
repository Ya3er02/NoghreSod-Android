// Build Variants Configuration for NoghreSod App
// This file contains all environment-specific configurations

import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.noghre.sod"
    compileSdk = 34

    // Load local.properties
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(FileInputStream(localPropertiesFile))
    }

    defaultConfig {
        applicationId = "com.noghre.sod"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Default BuildConfig fields
        buildConfigField("String", "API_BASE_URL", "\"https://api.noghresod.com/api/v1/\"")
        buildConfigField("long", "API_TIMEOUT", "30L")
        buildConfigField("boolean", "ENABLE_LOGGING", "false")
        
        // ============================================
        // Payment Gateway Secrets (from local.properties)
        // ============================================
        
        // Zarinpal Payment Gateway
        val zarinpalMerchantId = localProperties.getProperty(
            "zarinpal.merchant.id",
            "YOUR_PRODUCTION_MERCHANT_ID"
        )
        val zarinpalSandboxMerchantId = localProperties.getProperty(
            "zarinpal.sandbox.merchant.id",
            "YOUR_SANDBOX_MERCHANT_ID"
        )
        buildConfigField(
            "String",
            "ZARINPAL_MERCHANT_ID",
            "\"$zarinpalMerchantId\""
        )
        buildConfigField(
            "String",
            "ZARINPAL_SANDBOX_MERCHANT_ID",
            "\"$zarinpalSandboxMerchantId\""
        )
        
        // Firebase Configuration (if needed)
        val firebaseProjectId = localProperties.getProperty("firebase.project.id", "")
        if (firebaseProjectId.isNotEmpty()) {
            buildConfigField("String", "FIREBASE_PROJECT_ID", "\"$firebaseProjectId\"")
        }
    }

    // Signing Configurations
    signingConfigs {
        create("release") {
            val keystoreFile = localProperties.getProperty("keystore.file", "")
            if (keystoreFile.isNotEmpty() && file(keystoreFile).exists()) {
                storeFile = file(keystoreFile)
                storePassword = localProperties.getProperty("keystore.password", "")
                keyAlias = localProperties.getProperty("keystore.alias", "")
                keyPassword = localProperties.getProperty("keystore.key.password", "")
            } else {
                println("⚠️ WARNING: Release keystore not configured. Using debug signing.")
                // Fallback to debug signing
                storeFile = file("${System.getProperty("user.home")}/.android/debug.keystore")
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }
        }
    }

    // Build Types
    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"

            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"${localProperties.getProperty("dev.api.url", "https://dev-api.noghresod.com/api/v1/")}\""
            )
            buildConfigField(
                "long",
                "API_TIMEOUT",
                "${localProperties.getProperty("dev.api.timeout", "30")}L"
            )
            buildConfigField("boolean", "ENABLE_LOGGING", "true")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")

            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"${localProperties.getProperty("prod.api.url", "https://api.noghresod.com/api/v1/")}\""
            )
            buildConfigField(
                "long",
                "API_TIMEOUT",
                "${localProperties.getProperty("prod.api.timeout", "30")}L"
            )
            buildConfigField("boolean", "ENABLE_LOGGING", "false")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        create("staging") {
            initWith(getByName("release"))
            isDebuggable = false
            isMinifyEnabled = true
            applicationIdSuffix = ".staging"
            signingConfig = signingConfigs.getByName("release")

            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"${localProperties.getProperty("staging.api.url", "https://staging-api.noghresod.com/api/v1/")}\""
            )
            buildConfigField(
                "long",
                "API_TIMEOUT",
                "${localProperties.getProperty("staging.api.timeout", "30")}L"
            )
            buildConfigField("boolean", "ENABLE_LOGGING", "true")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Product Flavors for Environments
    flavorDimensions += "environment"

    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"${localProperties.getProperty("dev.api.url", "https://dev-api.noghresod.com/api/v1/")}\""
            )
            buildConfigField(
                "long",
                "API_TIMEOUT",
                "${localProperties.getProperty("dev.api.timeout", "30")}L"
            )
            
            // Use sandbox merchant ID for dev flavor
            val zarinpalSandboxId = localProperties.getProperty(
                "zarinpal.sandbox.merchant.id",
                "YOUR_SANDBOX_MERCHANT_ID"
            )
            buildConfigField(
                "String",
                "ZARINPAL_MERCHANT_ID",
                "\"$zarinpalSandboxId\""
            )
        }

        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"

            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"${localProperties.getProperty("staging.api.url", "https://staging-api.noghresod.com/api/v1/")}\""
            )
            buildConfigField(
                "long",
                "API_TIMEOUT",
                "${localProperties.getProperty("staging.api.timeout", "30")}L"
            )
            
            // Use sandbox merchant ID for staging flavor
            val zarinpalSandboxId = localProperties.getProperty(
                "zarinpal.sandbox.merchant.id",
                "YOUR_SANDBOX_MERCHANT_ID"
            )
            buildConfigField(
                "String",
                "ZARINPAL_MERCHANT_ID",
                "\"$zarinpalSandboxId\""
            )
        }

        create("production") {
            dimension = "environment"
            versionNameSuffix = "-prod"

            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"${localProperties.getProperty("prod.api.url", "https://api.noghresod.com/api/v1/")}\""
            )
            buildConfigField(
                "long",
                "API_TIMEOUT",
                "${localProperties.getProperty("prod.api.timeout", "30")}L"
            )
            
            // Use production merchant ID for production flavor
            val zarinpalProdId = localProperties.getProperty(
                "zarinpal.merchant.id",
                "YOUR_PRODUCTION_MERCHANT_ID"
            )
            buildConfigField(
                "String",
                "ZARINPAL_MERCHANT_ID",
                "\"$zarinpalProdId\""
            )
        }
    }
}
