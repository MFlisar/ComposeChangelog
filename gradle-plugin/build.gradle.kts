repositories {
    mavenCentral()
}

plugins {
    `kotlin-dsl-base`
    `java-gradle-plugin`
    `maven-publish`
}

dependencies {
    implementation(":gradle-shared")
}

gradlePlugin {
    plugins {
        create("changelog-utils") {
            id = "changelog-utils"
            implementationClass = "com.michaelflisar.composechangelog.gradle.plugin.ClassLoaderPlugin"
        }
    }
}

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