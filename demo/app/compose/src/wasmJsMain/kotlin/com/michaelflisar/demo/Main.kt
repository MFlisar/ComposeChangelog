package com.michaelflisar.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.michaelflisar.composechangelog.ChangelogDefaults
import com.michaelflisar.composechangelog.interfaces.IChangelogStateSaver

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
suspend fun main() {

    Shared.registerHeaderRenderer { group, name -> Icons.Default.Info /* for the sake of simplicity we simply use a single icon here always... */ }

    val setup = ChangelogDefaults.setup(
        logFileReader = { Shared.readChangelog() },
        versionFormatter = Shared.CHANGELOG_FORMATTER,
    )

    // 4) Application
    ComposeViewport(
        // mit container id geht es nicht --> wäre aber gut, dann würde ein Loader angezeigt werden, aktuell wird der nicht angezeigt...
        // viewportContainerId = wasmSetup.canvasElementId
    ) {
        val state = remember { mutableLongStateOf(0L) }
        val changelogStateSaver = object : IChangelogStateSaver {
            @Composable
            override fun collectLastShownVersion(): State<Long> = remember(state.value) { derivedStateOf { state.value } }

            override suspend fun lastShownVersion() = state.value

            override suspend fun saveLastShownVersion(version: Long) {
                state.value = version
            }
        }

        DemoApp(
            platform = "WASM",
            setup = setup,
            changelogStateSaver = changelogStateSaver
        )
    }
}