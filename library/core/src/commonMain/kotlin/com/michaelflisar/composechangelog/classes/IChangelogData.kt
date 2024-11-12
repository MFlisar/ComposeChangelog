package com.michaelflisar.composechangelog.classes

sealed interface IChangelogData {
    val id: Int
    val filter: String?
}