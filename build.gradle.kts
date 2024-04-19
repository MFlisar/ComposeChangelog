// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath(libs.gradle)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.kotlin.serialization)
    }
}


tasks.register("publishToMavenLocal") {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":shared:publishToMavenLocal"))
    dependsOn(gradle.includedBuild("gradle-plugin").task(":plugin:publishToMavenLocal"))
    //dependsOn(gradle.includedBuilds.map { it.task(":publishToMavenLocal") })
}

