repositories {
    mavenCentral()
}

group = "gradle-plugin"

plugins {
    `kotlin-dsl-base`
    `java-library`
    //`maven-publish`
}

// does not get published by itself
/*
project.afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "gradle-shared"
                from(components["java"])
            }
        }
    }
}*/