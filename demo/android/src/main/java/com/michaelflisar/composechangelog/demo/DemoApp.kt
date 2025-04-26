package com.michaelflisar.composechangelog.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.renderer.header.ChangelogHeaderRenderer
import com.michaelflisar.toolbox.androiddemoapp.DemoApp

class DemoApp : DemoApp() {
    override fun onCreate() {
        super.onCreate()

        Changelog.registerRenderer(
            ChangelogHeaderRenderer {

                val parts = it?.split(".") ?: return@ChangelogHeaderRenderer
                if (parts.isEmpty()) return@ChangelogHeaderRenderer

                val group = if (parts.size == 2) parts[0] else ""
                val name = if (parts.size == 1) parts[0] else parts[1]

                val icon = try {
                    getMaterialIconFromName(group, name)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }

                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }

            }
        )
    }
}

// helper function to get icons from androidx.compose.material.icons.*
// using reflection....
fun getMaterialIconFromName(group: String, name: String): ImageVector? {
    val cls = when (group) {
        "",
        "Filled" -> Icons.Filled
        "Outlined" -> Icons.Outlined
        "Sharp" -> Icons.Sharp
        "Rounded" -> Icons.Rounded
        "TwoTone" -> Icons.TwoTone
        else -> throw RuntimeException("Unknown group: $group")
    }

    val grp = cls.javaClass.simpleName.lowercase()
    val className = "androidx.compose.material.icons.$grp.${name}Kt"

    val cl = Class.forName(className)
    val method = cl.declaredMethods.first()
    return method.invoke(null, cls) as ImageVector
}