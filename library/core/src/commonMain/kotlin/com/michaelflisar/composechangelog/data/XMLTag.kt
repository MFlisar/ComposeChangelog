package com.michaelflisar.composechangelog.data

data class XMLTag(
    val tag: String,
    val attributes: List<XMLAttribute>,
    val innerText: String,
    val children: List<XMLTag>
) {
    fun findAttribute(name: String): XMLAttribute? {
        return attributes.find { it.name == name }
    }
}