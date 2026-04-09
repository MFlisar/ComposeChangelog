import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.format.DefaultVersionFormatter
import com.michaelflisar.kmpdevtools.BuildFileUtil
import com.michaelflisar.kmpdevtools.configs.*

plugins {
    // kmp + app/library
    alias(libs.plugins.android.application)
    // org.jetbrains.kotlin
    alias(libs.plugins.jetbrains.kotlin.compose)
    // org.jetbrains.compose
    // --
    // docs, publishing, validation
    // --
    // build tools
    alias(deps.plugins.kmpdevtools.buildplugin)
    //alias(deps.plugins.changelog.gradleplugin)
    // others
    // ...
}

// ------------------------
// Setup
// ------------------------

// example to parse any version string to a integer inside a build.gradle.kts file
//val appVersionCode = Changelog.buildVersionCode("1.0.0", DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatch))

val module = AppModuleConfig.readManual(project)

val androidConfig = AndroidAppConfig(
    compileSdk = app.versions.compileSdk,
    minSdk = app.versions.minSdk,
    targetSdk = app.versions.targetSdk
)

// -------------------
// Configurations
// -------------------

android {

    BuildFileUtil.setupAndroidApp(
        appModuleConfig = module,
        androidAppConfig = androidConfig,
        generateResAppName = true,
        buildConfig = true,
        checkDebugKeyStoreProperty = true,
        setupBuildTypesDebugAndRelease = true
    )
}

dependencies {

    implementation(libs.androidx.activity.compose)

    // Library
    implementation(project(":demo:app:compose"))
}