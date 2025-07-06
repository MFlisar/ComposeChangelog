dependencyResolutionManagement {

    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }

    versionCatalogs {
        create("app") {
            from(files("../../gradle/app.versions.toml"))
        }
        create("androidx") {
            from(files("../../gradle/androidx.versions.toml"))
        }
        create("kotlinx") {
            from(files("../../gradle/kotlinx.versions.toml"))
        }
        create("deps") {
            from(files("../../gradle/deps.versions.toml"))
        }
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
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

include(":plugin")
include(":shared")