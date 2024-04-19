dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

include(":gradle-plugin")
project(":gradle-plugin").projectDir = file("plugin")
include(":gradle-plugin-shared")
project(":gradle-plugin-shared").projectDir = file("../shared")