repositories {
    mavenCentral()
}

plugins {
    `kotlin-dsl-base`
    `java-library`
    `maven-publish`
}

/*
gradlePlugin {
    plugins {
        create("changelog-utils-shared") {
            id = "changelog-utils-shared"
            implementationClass = "com.michaelflisar.composechangelog.gradle.plugin.ClassLoaderPluginShared"
        }
    }
}*/


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