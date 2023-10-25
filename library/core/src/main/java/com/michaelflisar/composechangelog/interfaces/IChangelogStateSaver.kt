package com.michaelflisar.composechangelog.interfaces

interface IChangelogStateSaver {
    suspend fun lastShownVersion(): Long
    suspend fun saveLastShownVersion(version: Long)
}