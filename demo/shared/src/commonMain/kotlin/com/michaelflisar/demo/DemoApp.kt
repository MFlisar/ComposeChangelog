package com.michaelflisar.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.classes.rememberChangelogState
import com.michaelflisar.composechangelog.demo.BuildKonfig
import com.michaelflisar.composechangelog.interfaces.IChangelogStateSaver
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoApp(
    platform: String,
    setup: Changelog.Setup,
    changelogStateSaver: IChangelogStateSaver,
) {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(BuildKonfig.appName) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            DemoContent(
                modifier = Modifier.padding(paddingValues),
                platform = platform,
                versionName = BuildKonfig.versionName,
                setup = setup,
                changelogStateSaver = changelogStateSaver
            )
        }
    }
}

@Composable
private fun DemoContent(
    modifier: Modifier,
    platform: String,
    versionName: String,
    setup: Changelog.Setup,
    changelogStateSaver: IChangelogStateSaver,
) {
    val scope = rememberCoroutineScope()

    // saver for the automatic changelog showing
    val lastChangelog by changelogStateSaver.collectLastShownVersion()

    val changelogState = rememberChangelogState()

    // initially we check if we need to show the changelog
    // this is optional of course...
    // does not show the log if the current version is the "install version"
    LaunchedEffect(Unit) {
        changelogState.checkShouldShowChangelogOnStart(
            stateSaver = changelogStateSaver,
            versionName = versionName,
            versionFormatter = setup.versionFormatter
        )
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column {
            Text("Platform: $platform", fontWeight = FontWeight.Bold)
            Text("App Version", fontWeight = FontWeight.Bold)
            Text(
                text = "Code: ${setup.versionFormatter.parseVersion(versionName)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Name: $versionName",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Last Changelog: $lastChangelog",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Button(onClick = {
            changelogState.show()
        }) {
            Text("Show Changelog")
        }
        Button(onClick = {
            scope.launch {
                changelogStateSaver.saveLastShownVersion(
                    setup.versionFormatter.parseVersion("0.9.0").toLong()
                )
            }
        }) {
            Text("Reset Last Changelog to 0.9.0")
        }
        Button(onClick = {
            scope.launch {
                changelogState.checkShouldShowChangelogOnStart(
                    stateSaver = changelogStateSaver,
                    versionName = versionName,
                    versionFormatter = setup.versionFormatter
                )
            }
        }) {
            Text("Check if changelog should be shown")
        }
    }

    // show changelog dialog
    if (changelogState.visible) {
        Dialog(
            onDismissRequest = { changelogState.hide() }
        ) {
            Surface {
                Changelog(changelogState, setup, Modifier.fillMaxWidth())
            }
        }
    }
}