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