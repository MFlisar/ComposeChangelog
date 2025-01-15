---
icon: material/button-pointer
---

This plugin allows you to transform a version string into a version code. This is useful if you want to use a version string in your gradle files and want to calculate the version code based on it.

#### Example

Top level `build.gradle.kts` file:

```kotlin
plugins {
    id("io.github.mflisar.composechangelog.gradle-plugin") apply false
}
```

After adding above you can simple apply the plugin inside your apps `build.gradle.kts` file like following and use it like following:

```kotlin
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.DefaultVersionFormatter

plugins {
    // other plugins
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")

    // plugin of compose changelog
    id("io.github.mflisar.composechangelog.gradle-plugin")
}

// define your app version code based on your format, here we use the Major.Minor.Patch format
val version = "0.3.0"
val code = Changelog.buildVersionCode(version, DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatch))

// make sure to use the SAME formatter inside your code whenever you want to show a changelog - Major.Minor.Patch format is the default one though

android {

    // ...

    defaultConfig {
        // use the version and code variables from above
        versionCode = code
        versionName = version
    }

     // ...
}
```

You now only must change `val version = "0.3.0"` to whatever new version you want and the code will be calculated by itself.

Additionally you can easily use the `versionName` tag inside your `changelog.xml` file, the formatter will correctly parse it to it's number for you.

**Always make sure to use the same formatter in your `build.gradle.kts` as well as inside your code.**