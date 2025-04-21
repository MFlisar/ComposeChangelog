// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.gradle.maven.publish.plugin) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    `kotlin-dsl-base` apply false
    //`java-gradle-plugin` apply false
}
/*
tasks.register("publishToMavenLocal") {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":gradle-plugin:plugin:publishToMavenLocal"))
    //dependsOn(gradle.includedBuild("gradle-plugin").task(":gradle-plugin-shared:publishToMavenLocal"))
}

tasks.register("publishToMaven") {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":gradle-plugin:plugin:publishToMaven"))
    //dependsOn(gradle.includedBuild("gradle-plugin").task(":gradle-plugin-shared:publishToMavenLocal"))
}

tasks.register("publish") {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":gradle-plugin:plugin:publish"))
    //dependsOn(gradle.includedBuild("gradle-plugin").task(":gradle-plugin-shared:publishToMavenLocal"))
}*/

tasks.register("publish") {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":plugin:publish"))
}

tasks.register("publishToMaven") {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":plugin:publishToMaven"))
}

tasks.register("publishToMavenLocal") {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":plugin:publishToMavenLocal"))
}