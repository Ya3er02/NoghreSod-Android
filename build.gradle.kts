plugins {
    id("org.jetbrains.dokka") version "1.9.10"
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
    dokkaSourceSets {
        named("main") {
            moduleName.set("NoghreSod Android API")
        }
    }
}
