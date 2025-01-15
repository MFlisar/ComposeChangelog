---
icon: material/keyboard
---

#### Define your changelog as xml file (on android, a raw xml is preferred!)

```xml
  <changelog>

    <!-- with version names (PREFFERED) -->
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

#### Show the interesting parts of the changelog on app start

```kotlin
 // 1) we need a state saver to persist the version for which the changelog was last shown
// use either of the following 2 or implement the corresponding interface yourself

// saves the last shown version inside a preference file
val changelogStateSaver = remember {
    ChangelogStateSaverPreferences.create(context)
}

// ALTERNATIVE: if you use my kotpreference library like this demo you can do following:
val changelogStateSaverKotPrefs = remember {
    ChangelogStateSaverKotPreferences(DemoPrefs.lastShownVersionForChangelog)
}

// 2) optional - here you can apply some customisations like changelog resource id, localized texts, styles, filter, sorter, renderer...
val setup = ChangelogDefaults.setup()

// 3) show the changelog for the app start - this will only show the changelogs that the user did not see yet
val versionName = ChangelogUtil.getAppVersionName(context)
ShowChangelogDialogIfNecessary(changelogStateSaver, versionName, setup)
```

#### Show the full changelog

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
    ShowChangelogDialog(setup) {
        // this is the dismiss callback, here we must reset the showChangelog flag
        showChangelog = false
    }
}
```