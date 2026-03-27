package com.michaelflisar.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoApp(
                platform = "Windows",
                setup = App.ChangelogSetup,
                changelogStateSaver = App.ChangelogStateSaver(this)
            )
        }
    }
}