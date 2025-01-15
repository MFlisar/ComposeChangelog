---
icon: material/puzzle
---

This module provides the functionality to save and use the last show changelog version. It uses my `KotPreferences` library for this purpose.

```kotlin
val changelogStateSaverKotPrefs = ChangelogStateSaverKotPreferences(DemoPrefs.lastShownVersionForChangelog) // pass in any StorageSetting<Boolean> from KotPreferences

// use this state saver with ComposeChangelog
```