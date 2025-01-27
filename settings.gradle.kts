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
        create("androidx") {
            from(files("gradle/androidx.versions.toml"))
        }
        create("kotlinx") {
            from(files("gradle/kotlinx.versions.toml"))
        }
        create("deps") {
            from(files("gradle/deps.versions.toml"))
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

include(":composechangelog:core")
project(":composechangelog:core").projectDir = file("library/core")
include(":composechangelog:modules:statesaver:preferences")
project(":composechangelog:modules:statesaver:preferences").projectDir = file("library/modules/statesaver/preferences")
include(":composechangelog:modules:statesaver:kotpreferences")
project(":composechangelog:modules:statesaver:kotpreferences").projectDir = file("library/modules/statesaver/kotpreferences")

//include(":gradle-plugin-shared")
//project(":gradle-plugin-shared").projectdir = file("library/shared")
include(":composechangelog:shared")
project(":composechangelog:shared").projectDir = file("library/shared")

// --------------
// Demo
// --------------

include(":demo:android")
include(":demo:desktop")
