plugins {
    `kotlin-dsl-base`
    `java-library`
    `maven-publish`
}

group = "ComposeChangelog"

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