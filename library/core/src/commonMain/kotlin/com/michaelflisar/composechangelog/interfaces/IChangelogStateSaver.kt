package com.michaelflisar.composechangelog.interfaces

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

interface IChangelogStateSaver {
    @Composable
    fun collectLastShownVersion() : State<Long>
    suspend fun lastShownVersion(): Long
    suspend fun saveLastShownVersion(version: Long)
}