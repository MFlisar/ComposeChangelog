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
    alias(deps.plugins.kmpdevtools.buildplugin)
}

// ------------------------
// Setup
// ------------------------

val module = LibraryModuleConfig.read(project)

val moduleConfig = module.libraryConfig.getModuleForProject(rootProject.projectDir, project.projectDir)
val artifactId = moduleConfig.artifactId
val groupID = module.libraryConfig.maven.groupId

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

// maven publish configuration
if (BuildFileUtil.checkGradleProperty(project, "publishToMaven") != false) {
    val platform = GradlePlugin(
        javadocJar = JavadocJar.Dokka("dokkaGenerateHtml"),
        sourcesJar = SourcesJar.Sources()
    )
    BuildFileUtil.setupMavenPublish(module, platform)
}