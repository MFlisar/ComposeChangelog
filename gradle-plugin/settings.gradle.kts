pluginManagement {

    // repositories for build
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("app") {
            from(files("../gradle/app.versions.toml"))
        }
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

include(":gradle-plugin")
project(":gradle-plugin").projectDir = file("plugin")
include(":gradle-plugin-shared")
project(":gradle-plugin-shared").projectDir = file("../library/shared")