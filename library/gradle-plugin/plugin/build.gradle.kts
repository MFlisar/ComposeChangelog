import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    //alias(libs.plugins.kotlin.jvm)
    `kotlin-dsl-base`
    `java-gradle-plugin`
    alias(libs.plugins.dokka)
    alias(libs.plugins.gradle.maven.publish.plugin)
}

// -------------------
// Informations
// -------------------

val description = "a gradle plugin that provides common functions for gradle files"

// Module
val artifactId = "gradle-plugin"

// Library
val libraryName = "ComposeChangelog"
val libraryDescription = "ComposeChangelog - $artifactId module - $description"
val groupID = "io.github.mflisar.composechangelog"
val release = 2023
val github = "https://github.com/MFlisar/ComposeChangelog"
val license = "Apache License 2.0"
val licenseUrl = "$github/blob/main/LICENSE"

// -------------------
// Variables for Documentation Generator
// -------------------

// # DEP is an optional arrays!

// OPTIONAL = "true"                // defines if this module is optional or not
// GROUP_ID = "gradle-plugin"             // defines the "grouping" in the documentation this module belongs to
// #DEP = "deps.kotbilling|KotBilling|https://github.com/MFlisar/Kotbilling"
// PLATFORM_INFO = "this is a gradle plugin only"               // defines a comment that will be shown in the documentation for this modules platform support

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

mavenPublishing {

    configure(
        GradlePlugin(
            javadocJar = JavadocJar.Dokka("dokkaHtml"),
            sourcesJar = true
        )
    )

    coordinates(
        groupId = groupID,
        artifactId = artifactId,
        version = System.getenv("TAG")
    )

    pom {
        name.set(libraryName)
        description.set(libraryDescription)
        inceptionYear.set("$release")
        url.set(github)

        licenses {
            license {
                name.set(license)
                url.set(licenseUrl)
            }
        }

        developers {
            developer {
                id.set("mflisar")
                name.set("Michael Flisar")
                email.set("mflisar.development@gmail.com")
            }
        }

        scm {
            url.set(github)
        }
    }

    // Configure publishing to Maven Central
    publishToMavenCentral(true)

    // Enable GPG signing for all publications
    signAllPublications()
}