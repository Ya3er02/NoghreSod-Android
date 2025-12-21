plugins {
    id("jacoco")
}

jacoco {
    toolVersion = "0.8.10"
}

val jacocoTestReport = tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.withType<Test>())

    afterEvaluate {
        classDirectories.setFrom(
            files(
                fileTree(
                    "${layout.buildDirectory}/intermediates/classes/debug",
                ) {
                    exclude(
                        "**/R.class",
                        "**/R\$*.class",
                        "**/BuildConfig.*",
                        "**/Manifest*.*",
                        "**/*Test*.*",
                        "android/**/*.*",
                        "androidx/**/*.*",
                        "**/*\$*.class",
                    )
                }
            )
        )
    }

    val sourceDirs = files(
        "src/main/kotlin",
        "src/main/java"
    )

    sourceDirectories.setFrom(sourceDirs)
    executionData.setFrom(
        fileTree("${layout.buildDirectory}") {
            include("jacoco/test*.exec")
        }
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

val jacocoVerificationReport = tasks.register<JacocoVerificationReport>("jacocoVerificationReport") {
    dependsOn(jacocoTestReport)

    afterEvaluate {
        classDirectories.setFrom(
            files(
                fileTree(
                    "${layout.buildDirectory}/intermediates/classes/debug",
                ) {
                    exclude(
                        "**/R.class",
                        "**/R\$*.class",
                        "**/BuildConfig.*",
                        "**/Manifest*.*",
                        "**/*Test*.*",
                        "android/**/*.*",
                        "androidx/**/*.*",
                        "**/*\$*.class",
                    )
                }
            )
        )
    }

    val sourceDirs = files(
        "src/main/kotlin",
        "src/main/java"
    )

    sourceDirectories.setFrom(sourceDirs)
    executionData.setFrom(
        fileTree("${layout.buildDirectory}") {
            include("jacoco/test*.exec")
        }
    )

    violationRules {
        rule {
            element = "PACKAGE"
            excludes = listOf(
                "com.noghre.sod.ui.*",
                "com.noghre.sod.di.*",
            )

            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.50".toBigDecimal()
            }
        }

        rule {
            element = "CLASS"
            includes = listOf(
                "com.noghre.sod.data.repository.*",
                "com.noghre.sod.viewmodel.*",
            )

            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

val coverageReport = tasks.register("coverageReport") {
    dependsOn(jacocoVerificationReport)
    doLast {
        println("\n✅ Code coverage report generated at: build/reports/jacoco/test/html/index.html")
    }
}

tasks.named("check") {
    dependsOn(coverageReport)
}
