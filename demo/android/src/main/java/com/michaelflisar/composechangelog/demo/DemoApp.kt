package com.michaelflisar.composechangelog.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.renderer.header.ChangelogHeaderRenderer
import com.michaelflisar.toolbox.androiddemoapp.DemoApp

class DemoApp : DemoApp() {
    override fun onCreate() {
        super.onCreate()

        Changelog.registerRenderer(
            ChangelogHeaderRenderer {
                when (it) {
                    "info" -> { Icon(imageVector = Icons.Default.Info, contentDescription = null) }
                    else -> {}
                }
            }
        )
    }
}