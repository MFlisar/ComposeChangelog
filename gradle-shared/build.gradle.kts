repositories {
    mavenCentral()
}

plugins {
    `kotlin-dsl-base`
    `java-library`
    `java-gradle-plugin`
    `maven-publish`
}

gradlePlugin {
    plugins {
        create("changelog-utils") {
            id = "changelog-utils"
            implementationClass = "com.michaelflisar.composechangelog.gradle.plugin.ClassLoaderPluginShared"
        }
    }
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

// does not get published - used internally only
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