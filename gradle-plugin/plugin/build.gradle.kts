plugins {
    `kotlin-dsl-base`
    `java-library`
    `java-gradle-plugin`
    `maven-publish`
}

dependencies {
    api(project(":shared"))
}

gradlePlugin {
    plugins {
        create("compose-changelog") {
            id = "compose-changelog"
            implementationClass = "com.michaelflisar.composechangelog.gradle.plugin.ClassLoaderPlugin"
        }
        isAutomatedPublishing = false
    }
}

// should be published automatically
group = "gradle-plugin"

project.afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = "gradle-plugin"
                from(components["java"])
            }
        }
    }
}