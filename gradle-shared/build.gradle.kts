repositories {
    mavenCentral()
}

plugins {
    //id("org.jetbrains.kotlin.jvm")
    `kotlin-dsl-base`
    `java-library`
    `maven-publish`
}

project.afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "gradle-plugin-shared"
                from(components["java"])
            }
        }
    }
}