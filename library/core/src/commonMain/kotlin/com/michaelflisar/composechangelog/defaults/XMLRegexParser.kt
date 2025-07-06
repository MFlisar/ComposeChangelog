package com.michaelflisar.composechangelog.defaults

import com.michaelflisar.composechangelog.Changelog
import com.michaelflisar.composechangelog.ChangelogVersionFormatter
import com.michaelflisar.composechangelog.Constants
import com.michaelflisar.composechangelog.IODispatcher
import com.michaelflisar.composechangelog.data.ChangelogReleaseItem
import com.michaelflisar.composechangelog.data.XMLAttribute
import com.michaelflisar.composechangelog.data.XMLTag
import kotlinx.coroutines.withContext

object XMLRegexParser {

    private val REGEX_ATTRIBUTES = Regex("(\\w+)=\"(.*?)\"")

    suspend fun parse(
        logFileReader: suspend () -> ByteArray,
        versionFormatter: ChangelogVersionFormatter,
    ): List<ChangelogReleaseItem> {
        return withContext(Changelog.IODispatcher) {
            val bytes = logFileReader()
            val content = bytes.decodeToString()
            findAllReleaseTags(content)
                .map {

                    val attrVersionName = it.findAttribute(Constants.XML_ATTR_VERSION_NAME)?.value
                    val attrVersionCode = it.findAttribute(Constants.XML_ATTR_VERSION_CODE)?.value
                    val versionCode: Int
                    val versionName: String
                    if (attrVersionName != null && attrVersionCode != null) {
                        throw RuntimeException("Please only provide ${Constants.XML_ATTR_VERSION_NAME} OR ${Constants.XML_ATTR_VERSION_CODE}!")
                    } else if (attrVersionName != null) {
                        versionCode = versionFormatter.parseVersion(attrVersionName)
                        versionName = attrVersionName
                    } else {
                        versionCode = attrVersionCode!!.toInt()
                        versionName = versionFormatter.formatVersion(versionCode)
                    }
                    val attrTitle = it.findAttribute(Constants.XML_ATTR_TITLE)?.value
                    val attrDate = it.findAttribute(Constants.XML_ATTR_DATE)!!.value

                    val subTags = it.children

                    ChangelogReleaseItem(
                        versionCode,
                        versionName,
                        attrDate,
                        attrTitle,
                        subTags
                 )
            }
        }
    }

    private fun findAllReleaseTags(xml: String): List<XMLTag> {
        return findAllPlainNodes(xml, "release")
    }

    private fun findAllPlainNodes(xml: String): List<XMLTag> {
        val regex = Regex("<(\\w+)([^>]*)>([\\s\\S]*?)</\\1>")
        return regex.findAll(xml).map { match ->
            val tagName = match.groupValues[1]
            val attributesString = match.groupValues[2]
            val content = match.groupValues[3].trim()
            val attributes = parseAttributes(attributesString)
            val children = findAllPlainNodes(content)
            XMLTag(tagName, attributes, content, children)
        }.toList()
    }

    private fun findAllPlainNodes(xml: String, tag: String): List<XMLTag> {
        val regex = Regex("<$tag([^>]*)>([\\s\\S]*?)</$tag>")
        return regex.findAll(xml).map { match ->
            val attributesString = match.groupValues[1]
            val content = match.groupValues[2].trim()
            val attributes = parseAttributes(attributesString)
            val children = findAllPlainNodes(content)
            XMLTag(tag, attributes, content, children)
        }.toList()
    }

    private fun parseAttributes(attributesString: String): List<XMLAttribute> {
        return REGEX_ATTRIBUTES.findAll(attributesString).map {
            XMLAttribute(it.groupValues[1], it.groupValues[2])
        }.toList()
    }

}