import com.michaelflisar.composechangelog.gradle.plugin.Changelog
import com.michaelflisar.composechangelog.DefaultVersionFormatter

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    id("compose-changelog")
}

val version = "1.0.4"
val code = Changelog.buildVersionCode(version, DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatchCandidate))

android {

    namespace = "com.michaelflisar.composechangelog.demo"

    compileSdk = app.versions.compileSdk.get().toInt()

    buildFeatures {
        compose = true
    }

    defaultConfig {
        minSdk = app.versions.minSdk.get().toInt()
        targetSdk = app.versions.targetSdk.get().toInt()
        versionCode = code
        versionName = version
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    // eventually use local custom signing
    val debugKeyStore = providers.gradleProperty("debugKeyStore").orNull
    if (debugKeyStore != null) {
        signingConfigs {
            getByName("debug") {
                keyAlias = "androiddebugkey"
                keyPassword = "android"
                storeFile = File(debugKeyStore)
                storePassword = "android"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {

    // ------------------------
    // Kotlin
    // ------------------------

    implementation(libs.kotlin)

    // ------------------------
    // AndroidX
    // ------------------------

    // Compose
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)

    implementation(libs.androidx.activity.compose)
    implementation(libs.drawablepainter)
    //implementation(libs.ui.tooling)
    //implementation(libs.ui.tooling.preview)

    // ------------------------
    // Libraries
    // ------------------------

    implementation(project(":ComposeChangelog:Core"))
    implementation(project(":ComposeChangelog:Modules:StateSaverPreferences"))
    implementation(project(":ComposeChangelog:Modules:StateSaverKotPreferences"))

    // KotPreferences
    implementation(libs.kotpreferences.core)
    implementation(libs.kotpreferences.datastore)
    implementation(libs.kotpreferences.compose)

    // a minimal library that provides some useful composables that I use inside demo activities
    implementation(libs.toolbox.demo.app)
}