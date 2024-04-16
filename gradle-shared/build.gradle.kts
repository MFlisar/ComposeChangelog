repositories {
    mavenCentral()
}

plugins {
    `kotlin-dsl-base`
    `java-library`
    `maven-publish`
}

project.afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "gradle-shared"
                from(components["java"])
            }
        }
    }
}