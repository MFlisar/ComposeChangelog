repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath(":gradle-shared")
    }
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
            implementationClass = "ClassLoaderPlugin"
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