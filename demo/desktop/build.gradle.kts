import com.michaelflisar.composechangelog.gradle.plugin.Changelog
import com.michaelflisar.composechangelog.DefaultVersionFormatter

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
    id("io.github.mflisar.composechangelog.gradle-plugin")
}

val version = "1.0.4"
val code = Changelog.buildVersionCode(version, DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatchCandidate))

kotlin {

    jvm {
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {

                implementation(compose.desktop.currentOs)

                implementation(project(":ComposeChangelog:Core"))
                implementation(project(":ComposeChangelog:Modules:StateSaverPreferences"))
                implementation(project(":ComposeChangelog:Modules:StateSaverKotPreferences"))

                // KotPreferences
                //implementation(libs.kotpreferences.core)
                //implementation(libs.kotpreferences.datastore)
                //implementation(libs.kotpreferences.compose)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.michaelflisar.composechangelog.demo.MainKt"

        nativeDistributions {
            //targetFormats(TargetFormat.Exe)

            packageName = "com.michaelflisar.composechangelog"
            packageVersion = version

            modules("java.instrument", "jdk.unsupported")
        }
    }
}