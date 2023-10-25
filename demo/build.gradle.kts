plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
}

android {

    namespace = "com.michaelflisar.composechangelog.demo"

    compileSdk = app.versions.compileSdk.get().toInt()

    buildFeatures {
        compose = true
    }

    defaultConfig {
        minSdk = app.versions.minSdk.get().toInt()
        targetSdk = app.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = compose.versions.compiler.get()
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

    // Compose BOM
    implementation(platform(compose.bom))

    // Dependent on Compose BOM
    implementation(compose.material3)
    implementation(compose.activity)
    implementation(compose.material.extendedicons)
    implementation(compose.drawablepainter)
    implementation(compose.ui.tooling)
    implementation(compose.ui.tooling.preview)

    // ------------------------
    // Libraries
    // ------------------------

    val live = false
    val composeChangelog = "0.1"

    // release test
    if (live) {
        implementation("com.github.MFlisar.ComposeChangelog:core:$composeChangelog")
        implementation("com.github.MFlisar.ComposeChangelog:statesaver-preferences:$composeChangelog")
        implementation("com.github.MFlisar.ComposeChangelog:statesaver-kotpreferences:$composeChangelog")
    } else {
        implementation(project(":ComposeChangelog:Core"))
        implementation(project(":ComposeChangelog:Modules:StateSaverPreferences"))
        implementation(project(":ComposeChangelog:Modules:StateSaverKotPreferences"))
    }

    // KotPreferences
    implementation(deps.kotpreferences.core)
    implementation(deps.kotpreferences.datastore)
    implementation(deps.kotpreferences.compose)

    // ComposePreferences
    implementation(deps.composepreferences.core)
    implementation(deps.composepreferences.screen.bool)
    implementation(deps.composepreferences.screen.list)
    implementation(deps.composepreferences.extension.kotpreferences)
}