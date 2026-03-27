package com.michaelflisar.demo

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector

object JavaUtil {

    // helper function to get icons from androidx.compose.material.icons.*
    // using reflection....
    fun getMaterialIconFromName(group: String, name: String): ImageVector? {
        val cls = when (group) {
            "", "Filled" -> Icons.Filled
            "Outlined" -> Icons.Outlined
            "Sharp" -> Icons.Sharp
            "Rounded" -> Icons.Rounded
            "TwoTone" -> Icons.TwoTone
            else -> return null
        }

        val grp = cls.javaClass.simpleName.lowercase()
        val normalizedName = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val candidates = linkedSetOf(name, normalizedName)

        for (candidate in candidates) {
            val className = "androidx.compose.material.icons.$grp.${candidate}Kt"
            val icon = runCatching {
                val cl = Class.forName(className)
                val method = cl.declaredMethods.firstOrNull {
                    it.parameterCount == 1 && it.name.startsWith("get")
                } ?: return@runCatching null
                method.invoke(null, cls) as? ImageVector
            }.getOrNull()
            if (icon != null)
                return icon
        }

        return null
    }
}