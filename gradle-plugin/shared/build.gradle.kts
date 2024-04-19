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
                groupId = "gradle-plugin"
                artifactId = "gradle-plugin-shared"
                from(components["java"])
            }
        }
    }
}