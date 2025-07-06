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
    alias(deps.plugins.kmp.gradle.tools.gradle.plugin) apply false
}

tasks.register("publish") {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":plugin:publish"))
}

tasks.register("publishToMaven") {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":plugin:publishToMaven"))
}

tasks.register("publishToMavenLocal") {
    dependsOn(gradle.includedBuild("gradle-plugin").task(":plugin:publishToMavenLocal"))
}

// exclude all demo projects from CI builds
subprojects {
    if (project.path.contains(":demo:", ignoreCase = true) && System.getenv("CI") == "true") {
        tasks.configureEach {
            enabled = false
        }
    }
}

// ------------------------
// Build mkdocs
// ------------------------

buildscript {
    dependencies {
        classpath(deps.kmp.gradle.tools.docs)
    }
}

tasks.register("buildDocs") {
    doLast {
        // read env from build-mkdocs.yml
        val generatedDocsDir = project.findProperty("generatedDocsDir") as String? ?: "gen/docs"
        com.michaelflisar.kmpgradletools.docs.buildDocs(
            relativePathDocsCustom = "documentation",
            relativePathGeneratedDocsOutput = generatedDocsDir,
            relativeModulesPath = "library",
            relativeDemosPath = "demo",
            customOtherProjectsYamlUrl = "https://raw.githubusercontent.com/MFlisar/kmp-template/refs/heads/main/data/other-projects.yml"
        )

        println("Docs have been build!")
    }
}