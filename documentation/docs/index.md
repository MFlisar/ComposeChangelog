---
icon: material/information-variant
---

{% include 'header.md' %}

{% include 'description.md' %}

{% include 'region-features.md' %}

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

!!! info

    All features are splitted into separate modules, just include the modules you want to use!

{% include 'screenshots.md' %}

{% include 'modules.md' %}

{% include 'platforms.md' %}

!!! info

    `iOS` support is missing currently. If you want to help simply create a PR! If you need help, contact me by creating a new issue. You just need to write a simple xml parser (I've written one for android and jvm already - especially the one for jvm can probably be easily converted to an iOS version)

{% include 'demo.md' %}




