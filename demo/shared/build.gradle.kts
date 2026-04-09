import com.michaelflisar.kmpdevtools.Targets
import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.core.Platform
import com.michaelflisar.kmpdevtools.configs.*
import com.michaelflisar.kmpdevtools.setupDependencies
import com.michaelflisar.kmpdevtools.setupBuildKonfig

plugins {
    // kmp + app/library
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    // org.jetbrains.kotlin
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.parcelize)
    // org.jetbrains.compose
    alias(libs.plugins.jetbrains.compose)
    // docs, publishing, validation
    // --
    // build tools
    alias(deps.plugins.kmpdevtools.buildplugin)
    alias(libs.plugins.buildkonfig)
    // others
    // ...
}

// ------------------------
// Setup
// ------------------------

val module = LibraryModuleConfig.readManual(project)

val buildTargets = Targets(
    // mobile
    android = true,
    //iOS = true,
    // desktop
    windows = true,
    //macOS = true,
    // web
    wasm = true
)

val androidConfig = AndroidLibraryConfig.createFromPath(
    libraryModuleConfig = module,
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    enableAndroidResources = true
)

// ------------------------
// Kotlin
// ------------------------

buildkonfig {
    setupBuildKonfig(module.appConfig)
}

compose.resources {
    packageOfResClass = "${module.projectNamespace}.demo.shared.resources"
    publicResClass = true
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
}

kotlin {

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    //-------------
    // Targets
    //-------------

    buildTargets.setupTargetsLibrary(module)
    android {
        buildTargets.setupTargetsAndroidLibrary(module, androidConfig, this)
    }

    // -------
    // Sources
    // -------

    sourceSets {

        // ---------------------
        // custom source sets
        // ---------------------

        val notWasmMain by creating { dependsOn(commonMain.get()) }
        val javaMain by creating { dependsOn(commonMain.get()) }

        setupDependencies(buildTargets, sourceSets) {

            notWasmMain supportedBy !Platform.WASM

            javaMain supportedBy listOf(Platform.ANDROID, Platform.WINDOWS)

        }

        // ---------------------
        // dependencies
        // ---------------------

        commonMain.dependencies {

            // compose
            api(libs.jetbrains.compose.components.resources)
            api(libs.jetbrains.compose.runtime)
            api(libs.jetbrains.compose.material3)
            api(libs.jetbrains.compose.material.icons.core)
            api(libs.jetbrains.compose.material.icons.extended)

            // modules
            api(project(":composechangelog:core"))
            implementation(project(":composechangelog:modules:renderer:header"))

        }

        notWasmMain.dependencies {

            // modules
            api(project(":composechangelog:modules:statesaver:preferences"))

        }
    }
}