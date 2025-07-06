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
// Functions
// --------------

fun includeModule(path: String, name: String) {
    include(name)
    project(name).projectDir = file(path)
}

// --------------
// Gradle Plugin
// --------------

includeModule("gradle-plugin/shared", ":shared")

includeBuild("gradle-plugin") {
    dependencySubstitution {
        //substitute(project(":shared")).using(project(":shared"))
    }
}

// --------------
// Library
// --------------

includeModule("library/core", ":composechangelog:core")

// renderer
includeModule("library/modules/renderer/header", ":composechangelog:modules:renderer:header")

// Statesavers
includeModule("library/modules/statesaver/preferences", ":composechangelog:modules:statesaver:preferences")
includeModule("library/modules/statesaver/kotpreferences", ":composechangelog:modules:statesaver:kotpreferences")

// --------------
// Demo
// --------------

include(":demo:android")
include(":demo:desktop")
