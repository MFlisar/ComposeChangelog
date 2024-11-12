dependencyResolutionManagement {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }

    versionCatalogs {
        create("app") {
            from(files("gradle/app.versions.toml"))
        }
    }
}

pluginManagement {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}

// --------------
// Gradle Plugin
// --------------

includeBuild("gradle-plugin")

// --------------
// Library
// --------------

include(":ComposeChangelog:Core")
project(":ComposeChangelog:Core").projectDir = file("library/core")
include(":ComposeChangelog:Modules:StateSaverPreferences")
project(":ComposeChangelog:Modules:StateSaverPreferences").projectDir = file("library/modules/statesaver-preferences")
include(":ComposeChangelog:Modules:StateSaverKotPreferences")
project(":ComposeChangelog:Modules:StateSaverKotPreferences").projectDir = file("library/modules/statesaver-kotpreferences")

//include(":gradle-plugin-shared")
//project(":gradle-plugin-shared").projectDir = file("library/shared")
include(":ComposeChangelog:Shared")
project(":ComposeChangelog:Shared").projectDir = file("library/shared")

// --------------
// Demo
// --------------

include(":demo:android")
include(":demo:desktop")
