plugins {
    `kotlin-dsl-base`
    `java-library`
    `maven-publish`
}

group = "gradle-plugin"

project.afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "shared"
                from(components["java"])
            }
        }
    }
}