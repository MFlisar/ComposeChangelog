### About

[![Maven](https://img.shields.io/maven-central/v/io.github.mflisar.composechangelog/core?style=for-the-badge&color=blue)](https://central.sonatype.com/namespace/io.github.mflisar.composechangelog)
[![API](https://img.shields.io/badge/api-21%2B-brightgreen.svg?style=for-the-badge)](https://android-arsenal.com/api?level=21)
[![Kotlin](https://img.shields.io/github/languages/top/mflisar/kotpreferences.svg?style=for-the-badge&color=blueviolet)](https://kotlinlang.org/)
[![KMP](https://img.shields.io/badge/Kotlin_Multiplatform-blue?style=for-the-badge&label=Kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![License](https://img.shields.io/github/license/MFlisar/ComposeChangelog?style=for-the-badge)](LICENSE)

<h1 align="center">ComposeChangelog</h1>

This library offers you a a **changelog dialog** for compose including the ability to show new logs on app start only as well as filtering and customisation.

## :heavy_check_mark: Features

* filtering
    * useful to filter out uninteresting old changelog entries on app start
    * useful for filtering changelog based on build flavour
* also supports automatic handling of showing changelogs on app start (uses preference to save last seen changelog version and handles everything for you automatically to only show **new changelogs** and only show those once)
* customise look
    * you can provide custom composables for every item type if desired
    * you can provide custom version name formatter
    * you can provide a custom sorter
* supports raw and xml resources, default resource name is `changelog.xml` in raw folder
* supports summaries with a "show more" button
* optional provides a `gradle plugin` that allows you to convert version names automatically to version numbers

**All features are splitted into separate modules, just include the modules you want to use!**

## :camera: Screenshots

| ![Overview](screenshots/overview.jpg "Overview") |
| :-: |


## :link: Dependencies

|      Dependency       | Version |                     Infos                      |
|:---------------------:|:-------:|:----------------------------------------------:|
| Compose Multiplatform | `1.7.0` | based on compose `1.7.1` and material3 `1.3.0` |

> [!NOTE]  
> This library does not use any experimental compose APIs and therefore should be forward compatible with newer compose versions!

## :elephant: Gradle

This library is distributed via [maven central](https://central.sonatype.com/).

*build.gradle.kts*

```kts
val changelog = "<LATEST-VERSION>"

// core
implementation("io.github.mflisar.composechangelog:core:$changelog")

// modules
implementation("io.github.mflisar.composechangelog:statesaver-kotpreferences:$changelog")
implementation("io.github.mflisar.composechangelog:statesaver-preferences:$changelog")
```

## :zap: Modules

| Module                      | Info     | Description                                                                                          |
|-----------------------------|----------|------------------------------------------------------------------------------------------------------|
| `core`                      |          | the core module - must always be included                                                            |
| **Modules**                 |          |                                                                                                      |
| `statesaver-kotpreferences` | optional | saves the state of the last shown changelog inside a preference file using my KotPreferences library |
| `statesaver-preferences`    | optional | saves the state of the last shown changelog inside a preference file                                 |
| **Plugins**                 |          |                                                                                                      |
| `gradle-plugin`             | optional | a plugin that allows you to use version functions inside your build gradle files as well             |

## </> Basic Usage

<details open>
<summary>Define your changelog as raw xml file</summary>

```xml title="raw/changelog.xml"
  <changelog>
  
      <!-- with version names -->
      <release versionName="1.2.1" date="2024-04-20">
          <new type="summary">-new gradle plugin added - you don't need to convert version names to version codes anymore!</new>
      </release>
      <release versionName="1.2.0" date="2023-03-04">
          <info>Some info 1 - apostrophe test: it's weird, but apostrophes do not work in precompiled xml files placed in xml resources!</info>
          <new type="summary">Some improvement 1</new>
          <bugfix>Some bugfix 1</bugfix>
          <info>Some info 2</info>
          <new type="summary">Some improvement 2</new>
          <bugfix>Some bugfix 2</bugfix>
          <info>Some info 3</info>
          <new>Some improvement 3</new>
          <bugfix>Some bugfix 3</bugfix>
          <customTag>My custom tag text...</customTag>
      </release>

      <!-- with version codes --> 
      <release versionCode="118" date="2023-03-04">
          <new type="summary">This version has a summary item only - no show more button will be shown even if show more buttons are enabled</new>
      </release>
      <release versionCode="115" date="2023-03-04">
          <info>Some info</info>
          <new type="summary">Some improvement</new>
          <bugfix>Some bugfix</bugfix>
      </release>
      <release versionCode="110" versionName="Version 1.10" date="2023-03-03" filter="dogs">
          <info>Some dogs info - filter only set in release tag</info>
          <new type="summary">Some dogs improvement - filter only set in release tag</new>
          <bugfix>Some dogs bugfix - filter only set in release tag</bugfix>
      </release>
      <release versionCode="105" versionName="Version 1.05" date="2023-03-02" filter="cats">
          <info type="summary">single summary of version 1.05</info>
          <info>Some cats info - filter only set in release tag</info>
          <new>Some cats improvement - filter only set in release tag</new>
          <bugfix>Some cats bugfix - filter only set in release tag</bugfix>
      </release>
      <release versionCode="100" versionName="First release" date="2023-03-01">
          <info filter="cats" type="summary">single cats summary of version 1.00</info>
          <info filter="dogs" type="summary">single dogs summary of version 1.00</info>
          <info filter="cats">New cats added - this info has filter text 'cats'</info>
          <info filter="dogs">New dogs added - this info has filter text 'dogs'</info>
          <new filter="cats">Some cats improvement - this info has filter text 'cats'</new>
          <new filter="dogs">Some dogs improvement - this info has filter text 'dogs'</new>
          <bugfix filter="cats">Some cats bugfix - this info has filter text 'cats'</bugfix>
          <bugfix filter="dogs">Some dogs bugfix - this info has filter text 'dogs'</bugfix>
      </release>
      <release versionCode="90" versionName="First beta" date="2023-02-01">
          <info>this release does not have any summary item and will be shown expanded even if summary is enabled - this behaviour can be adjusted by the second parameter in the builder with which you enable summaries</info>
      </release>
  </changelog>
  ```

</details>

<details open>
<summary>Show the interesting parts of the changelog on app start</summary>

 ```kotlin
  // 1) we need a state saver to persist the version for which the changelog was last shown
  // use either of the following 2 or implement the corresponding interface yourself
  val changelogStateSaver = ChangelogStateSaverPreferences(LocalContext.current)
  val changelogStateSaverKotPrefs = ChangelogStateSaverKotPreferences(AppPrefs.lastShownVersionForChangelog)

  // 2) optional - here you can apply some customisations like changelog resource id, localized texts, styles, filter, sorter, renderer...
  val setup = ChangelogDefaults.setup()

  // 3) show the changelog for the app start - this will only show the changelogs that the user did not see yet
  Changelog.CheckedShowChangelog(changelogStateSaver, setup)
  ```
    
</details>

<details open>
<summary>Show the full changelog</summary>

```kotlin
// 1) we need a state to decide if we need to show the changelog or not
var showChangelog by remember { mutableStateOf(false) }

// 2) we need some event source
Button(onClick = { showChangelog = true }) {
    Text("Show Changelog")
}

// 3) we show the changelog if necessary
if (showChangelog) {
    // optional setup...
    val setup = ChangelogDefaults.setup()
    Changelog.ShowChangelogDialog(setup) {
        // this is the dismiss callback, here we must reset the showChangelog flag
        showChangelog = false
    }
}
```

</details>

<details open>
<summary>Use the plugin inside your gradle file</summary>

Project `build.gradle.kts` file:

```kotlin
buildscript {
    repositories {
        // repositories
        // ...
    }
    dependencies {
        // your classpaths...
        // ...

        // classpath for the gradle plugin of comose changelog
        classpath("io.github.mflisar.composechangelog:gradle-plugin:<VERSION>")
    }
}
```

After adding above you can simple apply the plugin inside your apps `build.gradle.kts` file like following and use it like following:

```kotlin
import com.michaelflisar.composechangelog.gradle.plugin.Changelog
import com.michaelflisar.composechangelog.DefaultVersionFormatter

plugins {
    // other plugins
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")

    // plugin of compose changelog
    id("compose-changelog")
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

</details>

## :computer: Supported Platforms

**Supported Platforms**

This is a **KMP (kotlin multiplatform)** library and the provided modules do support following platforms:

| Modules                   | Android | iOS | jvm | Information |
|:--------------------------|---------|-----|-----|-------------|
| core                      | √       |     |     | (1)         |
| statesaver-kotpreferences | √       | √   | √   |             |
| statesaver-preferences    | √       | √   | √   |             |

* (1) the XML parser I currently use is from the android package - it would be quite easily possible to provide implementations for other platforms (using ksoup e.g.), then the whole library would support all targets, but currently I do not need it and do not have time - if you want to do this task, just let me know.

## :tada: Demo

A full [demo](demo) is included inside the demo module, it shows nearly every usage with working examples.