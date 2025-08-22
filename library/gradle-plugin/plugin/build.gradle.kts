import com.michaelflisar.kmplibrary.BuildFilePlugin
import com.michaelflisar.kmplibrary.setupDependencies
import com.michaelflisar.kmplibrary.Target
import com.michaelflisar.kmplibrary.Targets
import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar

plugins {
    //alias(libs.plugins.kotlin.jvm)
    `kotlin-dsl-base`
    `java-gradle-plugin`
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
    alias(deps.plugins.kmplibrary.buildplugin)
}

// get build file plugin
val buildFilePlugin = project.plugins.getPlugin(BuildFilePlugin::class.java)

// -------------------
// Informations
// -------------------

val groupID = "io.github.mflisar.composechangelog"

// -------------------
// Setup
// -------------------

dependencies {
    api(project(":shared"))
}

gradlePlugin {
    plugins {
        create("$groupID.gradle-plugin") {
            id = "$groupID.gradle-plugin"
            implementationClass = "com.michaelflisar.composechangelog.gradle.plugin.ClassLoaderPlugin"
        }
        isAutomatedPublishing = true
    }
}

// -------------------
// Configurations
// -------------------

// maven publish configuration
if (buildFilePlugin.checkGradleProperty("publishToMaven") != false)
    buildFilePlugin.setupMavenPublish(
        platform = GradlePlugin(
            javadocJar = JavadocJar.Dokka("dokkaHtml"),
            sourcesJar = true
        )
    )


