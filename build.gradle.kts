plugins {
    id("org.jetbrains.dokka") version "2.1.0"
}

tasks.dokkaHtml.configure {
    outputDirectory.set(buildDir.resolve("dokka"))
    dokkaSourceSets {
        named("main") {
            moduleName.set("NoghreSod Android API")
        }
    }
}
