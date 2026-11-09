import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.configs.LibraryModuleConfig
import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar

plugins {
    //`kotlin-dsl`
    kotlin("jvm")
    `java-gradle-plugin`
    // docs, publishing, validation
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktech.maven.publish.base)
    // build tools
    alias(mflisar.plugins.kmpdevtools.buildplugin)
}

// ------------------------
// Setup
// ------------------------

val module = LibraryModuleConfig.read(project)

val moduleConfig = module.config.getModuleForProject(rootProject.projectDir, project.projectDir)
val artifactId = moduleConfig.artifactId
val groupID = module.config.publish.groupId

// -------------------
// Plugins
// -------------------

gradlePlugin {
    plugins {
        create("buildPlugin") {
            id = "$groupID.$artifactId"
            implementationClass = "com.michaelflisar.composechangelog.ChangelogPlugin"
        }
        isAutomatedPublishing = true
    }
}

dependencies {

    compileOnly(gradleApi())
    //implementation(gradleKotlinDsl())

    implementation(project(":composechangelog:shared"))
}

// -------------------
// Publish
// -------------------

BuildFileUtil.setupMavenPublish(
    module,
    GradlePlugin(
        javadocJar = JavadocJar.Dokka("dokkaGenerateHtml"),
        sourcesJar = SourcesJar.Sources()
    )
)