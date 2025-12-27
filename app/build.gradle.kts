// 📝 NOTE: Add these benchmark dependencies to build.gradle.kts under testImplementation:

// Existing dependencies...

// Add these for benchmark testing:
testImplementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
testImplementation("junit:junit:4.13.2")
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("app.cash.turbine:turbine:1.0.0")
testImplementation("com.google.truth:truth:1.1.5")
testImplementation("androidx.arch.core:core-testing:2.2.0")

// For UI/Benchmark tests:
androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.0")
androidTestImplementation("androidx.test.ext:junit:1.1.5")
androidTestImplementation("androidx.test:runner:1.5.2")
androidTestImplementation("androidx.test:rules:1.5.0")
