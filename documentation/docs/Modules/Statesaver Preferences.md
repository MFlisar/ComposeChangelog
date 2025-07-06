---
icon: material/puzzle
---

This module provides the functionality to save and use the last show changelog version. It uses the plain jetpack preferences to store the version code of the last shown changelog.

```kotlin
val changelogStateSaverKotPrefs = ChangelogStateSaverPreferences.create(context)

// use this state saver with ComposeChangelog
```