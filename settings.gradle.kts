dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    versionCatalogs {

        val kotlin = "1.9.22"
        val ksp = "1.9.22-1.0.20"
        val coroutines = "1.7.3"
        val gradle = "8.3.2"

        // TOML Files
        create("deps") {
            from(files("gradle/dependencies.versions.toml"))
        }
        create("compose") {
            from(files("gradle/compose.versions.toml"))
        }

        // Rest
        create("tools") {
            version("kotlin", kotlin)
            version("gradle", gradle)
            version("ksp", ksp)
        }
        create("app") {
            version("compileSdk", "34")
            version("minSdk", "21")
            version("targetSdk", "34")
        }
        create("libs") {
            // Kotlin
            library("kotlin", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin")
            library("kotlin.coroutines", "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
            library("kotlin.reflect", "org.jetbrains.kotlin:kotlin-reflect:$kotlin")
        }
    }
}

// --------------
// App
// --------------

//includeBuild("gradle-shared")
//includeBuild("gradle-plugin")

include(":ComposeChangelog:Core")
project(":ComposeChangelog:Core").projectDir = file("library/core")
include(":ComposeChangelog:Modules:StateSaverPreferences")
project(":ComposeChangelog:Modules:StateSaverPreferences").projectDir = file("library/modules/statesaver-preferences")
include(":ComposeChangelog:Modules:StateSaverKotPreferences")
project(":ComposeChangelog:Modules:StateSaverKotPreferences").projectDir = file("library/modules/statesaver-kotpreferences")

include(":ComposeChangelog:Plugin:Shared")
project(":ComposeChangelog:Plugin:Shared").projectDir = file("gradle-shared")
//include(":ComposeChangelog:Plugin:Gradle")
//project(":ComposeChangelog:Plugin:Gradle").projectDir = file("gradle-plugin")

if (!System.getenv().containsKey("JITPACK")) {
    include(":demo")
    project(":demo").projectDir = file("demo")
}
