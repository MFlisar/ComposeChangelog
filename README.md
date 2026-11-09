[![Maven Central](https://img.shields.io/maven-central/v/io.github.mflisar.composechangelog/core?color=blue)](https://central.sonatype.com/artifact/io.github.mflisar.composechangelog/core) ![API](https://img.shields.io/badge/api-24%2B-brightgreen.svg) ![Kotlin](https://img.shields.io/github/languages/top/MFlisar/ComposeChangelog.svg?color=blueviolet) ![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin_Multiplatform-blue?label=Kotlin)
# ComposeChangelog
![Platforms](https://img.shields.io/badge/PLATFORMS-black?style=for-the-badge)

This library provides following main features:

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

> [!TIP]
> Try the interactive demo:
> 
> [![Open Interactive Demo](https://img.shields.io/badge/ComposeChangelog_Demo-2962FF?style=for-the-badge&logo=googlechrome&logoColor=white)](https://mflisar.github.io/ComposeChangelog/demo/)

# Table of Contents

- [Supported Platforms](#computer-supported-platforms)
- [Versions](#arrow_right-versions)
- [Setup](#wrench-setup)
- [Usage](#rocket-usage)
- [Demo](#sparkles-demo)
- [API](#books-api)
- [Other Libraries](#bulb-other-libraries)

# :computer: Supported Platforms

| Module | Notes |
|---|---|
| core | provides all the basic classes and composables to show a changelog |
| renderer-header | implements rendering of a <header> tag |
| statesaver-kotpreferences | provides a simple state saver based on KotPreferences |
| statesaver-preferences | provides a simple state saver based on jetpack preferences |
| gradle-plugin | This is a gradle plugin that offers functions to generate version numbers from a version string |

# :arrow_right: Versions

| Dependency | Version |
|---|---|
| Kotlin | `2.4.0` |
| Jetbrains Compose | `1.11.1` |
| Jetbrains Compose Material3 | `1.9.0` |

# :wrench: Setup

<details open>

<summary><b>Using Version Catalogs</b></summary>

<br>

Define the dependencies inside your **libs.versions.toml** file.

```toml
[versions]

composechangelog = "<LATEST-VERSION>"

[plugins]

composechangelog-gradle-plugin = { id = "io.github.mflisar.composechangelog.gradle-plugin", version.ref = "composechangelog" }

[libraries]

composechangelog-core = { module = "io.github.mflisar.composechangelog:core", version.ref = "composechangelog" }
composechangelog-renderer-header = { module = "io.github.mflisar.composechangelog:renderer-header", version.ref = "composechangelog" }
composechangelog-statesaver-kotpreferences = { module = "io.github.mflisar.composechangelog:statesaver-kotpreferences", version.ref = "composechangelog" }
composechangelog-statesaver-preferences = { module = "io.github.mflisar.composechangelog:statesaver-preferences", version.ref = "composechangelog" }
```

And then use the definitions in your projects **build.gradle.kts** file like following:

```java
plugins {
    id(libs.composechangelog.gradle.plugin)
}

implementation(libs.composechangelog.core)
implementation(libs.composechangelog.renderer.header)
implementation(libs.composechangelog.statesaver.kotpreferences)
implementation(libs.composechangelog.statesaver.preferences)
```

</details>

<details>

<summary><b>Direct Dependency Notation</b></summary>

<br>

Simply add the dependencies inside your **build.gradle.kts** file.

```kotlin
val composechangelog = "<LATEST-VERSION>"

implementation("io.github.mflisar.composechangelog:core:${composechangelog}")
implementation("io.github.mflisar.composechangelog:renderer-header:${composechangelog}")
implementation("io.github.mflisar.composechangelog:statesaver-kotpreferences:${composechangelog}")
implementation("io.github.mflisar.composechangelog:statesaver-preferences:${composechangelog}")
plugins {
    id("io.github.mflisar.composechangelog.gradle-plugin") version "<LATEST-VERSION>"
}
```

</details>

# :rocket: Usage

#### Define your changelog as xml file

```xml
<changelog>

    <release versionName="2.0.0" date="2018-03-01" title="Major Update: Version 2.0.0">

        <!-- optional header -->
        <header icon="Info">
            <title>This version introduces a new feature: Cool New Feature</title>
            <infos>
                <item>Feature 1: theme the app the way you want it</item>
                <item>Feature 2: ...</item>
                <item>Feature 3: ...</item>
            </infos>
        </header>

        <!-- news -->
        <news>
            <item>New <b>backup function</b> added</item>
            <item>Added a progress indicator to the backup</item>
            <item>Some longer news with a very very very very very very very very very very very very long description to ensure that this item has multiple lines</item>
        </news>

        <!-- improvements -->
        <improvements>
            <item>Scrolling performance drastically improved</item>
            <item>Fixed a rare crash during export</item>
        </improvements>

        <!-- changes -->
        <changes>
            <item>Menu restructured - the export function was moved to the settings screen</item>
        </changes>

        <!-- bugfixes -->
        <bugfixes>
            <item>Fixed a possible rare crash during export</item>
            <item>Fixed a rare crash during export</item>
            <more>
                <item>Fixed a small internal bug</item>
                <item>Fixed another small internal bug</item>
                <item>And another one...</item>
                <item>And another one...</item>
                <item>And another one...</item>
                <item>And another one...</item>
            </more>
        </bugfixes>
    </release>

    <release versionName="1.0.1" date="2018-02-01">
        <!-- bugfixes -->
        <bugfixes>
            <item>Severe crash when opening the settings screen fixed</item>
        </bugfixes>
    </release>

    <release versionName="1.0.0" date="2018-01-01">
        <!-- optional header -->
        <header icon="Warning">
            <title>First release version</title>
            <item>We finally made it - this is the first stable release version</item>
        </header>
        <!-- news -->
        <news>
            <item>New backup function added</item>
            <item>Added a progress indicator to the backup</item>
        </news>
        <!-- improvements -->
        <improvements>
            <item>Scrolling performance drastically improved</item>
            <item>Fixed a rare crash during export</item>
        </improvements>
        <!-- changes -->
        <changes>
            <item>Menu restructured - the export function was moved to the settings screen</item>
        </changes>
        <!-- bugfixes -->
        <bugfixes>
            <item>Fixed a possible rare crash during export</item>
            <item>Fixed a rare crash during export</item>
            <more>
                <item>Fixed a small internal bug</item>
                <item>Fixed another small internal bug</item>
            </more>
        </bugfixes>
    </release>

</changelog>
```

> [!NOTE]
> the header tag only works if you add the `renderer-header` module!
> supported icons for the header tag must be defined by yourself (the icon is optional though)

#### Show the changelog

```kotlin

// 1) define a version formatter that can convert between a version number and a version string
val changelogFormatter = DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatchCandidate)

// 2) optional - here you can apply some customisations like changelog resource id, localized texts, styles, filter, sorter, renderer...
val setup = ChangelogDefaults.setup(context = context) // context must only be provided on android!

// 3) show the changelog dialog
if (changelogState.visible) {
    Dialog(
        onDismissRequest = { changelogState.hide() }
    ) {
        Surface {
            Changelog(changelogState, setup, Modifier.fillMaxWidth())
        }
    }
}

// 4) call changelogState.show() ...
Button(
    onClick = {
        changelogState.show() // this shows the full changelog
    }
) {
    Text("Show Changelog")
}

```

#### Show the interesting parts of the changelog on app start

In addition to the previous code, you can also show the changelog on app start if there are new versions that the user did not see yet. This is optional of course but it is a nice way to inform your users about new features and changes in your app.

> [!NOTE]
> This will automatically only show new changelogs that the user did not see yet.

```kotlin

// --------------------------------
// 1) we need a state saver to persist the version for which the changelog was last shown
// use either of the following 2 or implement the corresponding interface yourself
// --------------------------------

// saves the last shown version inside a preference file
val changelogStateSaver = remember {
    ChangelogStateSaverPreferences.create(context) // context must only be provided on android!
}

// ALTERNATIVE: if you use my kotpreference library like this demo you can do following:
val changelogStateSaverKotPrefs = remember {
    ChangelogStateSaverKotPreferences(DemoPrefs.lastShownVersionForChangelog)
}

// --------------------------------
// 2) use the statesaver to check and update the changelogState to show the changelog on app start if needed
// this will only show the changelogs that the user did not see yet
// --------------------------------

val versionName = Changelog.getAppVersionName(context)
val changelogState = rememberChangelogState()
LaunchedEffect(Unit) {
    // determines which changelog versions have not been shown to the user yet
    // + updates the state accordingly to show the changelog on app start if needed
    // + saves the latest shown version in the state saver
    changelogState.checkShouldShowChangelogOnStart(
        changelogStateSaver,
        versionName,
        changelogFormatter
    )
}

```

#### Header renderer

If you want to use the header renderer, add the module and register the renderer like following:

```kotlin
// in this example we define a renderer that can render 3 icons for the header - this optional
Changelog.registerRenderer(
    ChangelogHeaderRenderer {
        val icon = when (it?.lowercase()) {
            "info" -> Icons.Default.Info
            "new" -> Icons.Default.NewReleases
            "warning" -> Icons.Default.Warning
            else -> null
        }
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null)
        }
    }
)
```

# :sparkles: Demo

A [demo project](demo) is included.

# :books: API

Check out the [API documentation](https://MFlisar.github.io/ComposeChangelog/api/).

# :bulb: Other Libraries

You can find more of my multiplatform libraries that work well together [here](https://mflisar.github.io/Libraries/).

# :twisted_rightwards_arrows: Compatibility

When combining my libraries, you can find compatibility information [here](https://mflisar.github.io/Libraries/compatibilities/).
