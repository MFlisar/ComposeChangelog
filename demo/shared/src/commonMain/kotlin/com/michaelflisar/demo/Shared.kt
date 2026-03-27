package com.michaelflisar.demo

import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.demo.shared.resources.Res
import com.michaelflisar.composechangelog.format.DefaultVersionFormatter
import com.michaelflisar.composechangelog.renderer.header.ChangelogHeaderRenderer

object Shared {

    val CHANGELOG_FORMATTER =
        DefaultVersionFormatter(DefaultVersionFormatter.Format.MajorMinorPatchCandidate)

    suspend fun readChangelog(): ByteArray {
        return Res.readBytes("files/changelog.xml")
    }

    fun registerHeaderRenderer(
        provideIcon: (group: String, name: String) -> ImageVector?,
    ) {
        // register custom renderers
        Changelog.registerRenderer(
            ChangelogHeaderRenderer {

                val parts = it?.split(".") ?: return@ChangelogHeaderRenderer
                if (parts.isEmpty()) return@ChangelogHeaderRenderer

                val group = if (parts.size == 2) parts[0] else ""
                val name = if (parts.size == 1) parts[0] else parts[1]

                val icon = try {
                    provideIcon(group, name)
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