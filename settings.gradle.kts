dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    versionCatalogs {
        create("deps") {
            from(files("gradle/dependencies.versions.toml"))
        }
        create("compose") {
            from(files("gradle/compose.versions.toml"))
        }
        create("app") {
            from(files("gradle/app.versions.toml"))
        }
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

include(":ComposeChangelog:Shared")
project(":ComposeChangelog:Shared").projectDir = file("shared")

// --------------
// Demo
// --------------

include(":demo")
project(":demo").projectDir = file("demo")
