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

include(":shared")
project(":shared").projectDir = file("gradle-plugin/shared")

includeBuild("gradle-plugin") {
    dependencySubstitution {
        //substitute(project(":shared")).using(project(":shared"))
    }
}

// --------------
// Library
// --------------

include(":composechangelog:core")
project(":composechangelog:core").projectDir = file("library/core")

// renderer
include(":composechangelog:modules:renderer:header")
project(":composechangelog:modules:renderer:header").projectDir = file("library/modules/renderer/header")

// Statesavers
include(":composechangelog:modules:statesaver:preferences")
project(":composechangelog:modules:statesaver:preferences").projectDir = file("library/modules/statesaver/preferences")
include(":composechangelog:modules:statesaver:kotpreferences")
project(":composechangelog:modules:statesaver:kotpreferences").projectDir = file("library/modules/statesaver/kotpreferences")

// --------------
// Demo
// --------------

include(":demo:android")
include(":demo:desktop")
