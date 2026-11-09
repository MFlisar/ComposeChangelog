import com.michaelflisar.kmpdevtools.Targets
import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.core.Target
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
    alias(mflisar.plugins.kmpdevtools.buildplugin)
    alias(libs.plugins.buildkonfig)
    // others
    // ...
}

// ------------------------
// Setup
// ------------------------

val module = LibraryModuleConfig.read(project)
val appModule = AppModuleConfig.read(project)

val buildTargets = Targets(
    // mobile
    // iOS target intentionally disabled for the demo
    Target.ANDROID,
    // desktop
    Target.JVM,
    // web
    Target.WASM
)

val androidConfig = AndroidLibraryConfig.create(
    libraryModuleConfig = module,
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    enableAndroidResources = true
)

// ------------------------
// Kotlin
// ------------------------

buildkonfig {
    setupBuildKonfig(project = project, config = appModule.config)
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

        setupDependencies(module, buildTargets, sourceSets) {

            notWasmMain supportedBy !Target.WASM

            javaMain supportedBy listOf(Target.ANDROID, Target.JVM)

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