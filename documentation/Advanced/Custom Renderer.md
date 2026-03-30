If you want to support custom tags you can register custom renderers. The `header` module is an example for this.

The process is like following:

#### 1) Create a custom renderer

Simple create a class that extends by implementing the `IChangelogItemRenderer` interface. Here's an example of the header renderer:

[ChangelogHeaderRenderer](library\modules\renderer\header\src\commonMain\kotlin\com\michaelflisar\composechangelog\renderer\header\ChangelogHeaderRenderer.kt)

#### 2) register the renderer

```kotlin
val customRenderer = ...
Changelog.registerRenderer(customRenderer)
```
