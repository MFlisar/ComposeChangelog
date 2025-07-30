---
icon: material/keyboard
---

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
        <header icon="Outlined.Warning">
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

!!! info

    * the header tag only works if you add the `renderer-header` module!
    * supported icons for the header tag must be defined by yourself (the icon is optional though)

#### Show the interesting parts of the changelog on app start

```kotlin

// define a version formatter that can convert between a version number and a version string
val CHANGELOG_FORMATTER = DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatchCandidate)

 // 1) we need a state saver to persist the version for which the changelog was last shown
// use either of the following 2 or implement the corresponding interface yourself

// saves the last shown version inside a preference file
val changelogStateSaver = remember {
    ChangelogStateSaverPreferences.create(context) // context must only be provided on android!
}

// ALTERNATIVE: if you use my kotpreference library like this demo you can do following:
val changelogStateSaverKotPrefs = remember {
    ChangelogStateSaverKotPreferences(DemoPrefs.lastShownVersionForChangelog)
}

// 2) optional - here you can apply some customisations like changelog resource id, localized texts, styles, filter, sorter, renderer...
val setup = ChangelogDefaults.setup(context = context) // context must only be provided on android!

// 3) show the changelog for the app start - this will only show the changelogs that the user did not see yet
val versionName = Changelog.getAppVersionName(context)
val changelogState = rememberChangelogState()
// initially we check if we need to show the changelog
// this is optional of course...
LaunchedEffect(Unit) {
    changelogState.checkShouldShowChangelogOnStart(
        changelogStateSaver,
        versionName,
        CHANGELOG_FORMATTER
    )
}
```

#### Show the full changelog

```kotlin
Button(
    onClick = {
        changelogState.show()
    }
) {
    Text("Show Changelog")
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