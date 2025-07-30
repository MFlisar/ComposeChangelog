package com.michaelflisar.composechangelog.internal

import android.util.Xml
import com.michaelflisar.composechangelog.ChangelogVersionFormatter
import com.michaelflisar.composechangelog.Constants
import com.michaelflisar.composechangelog.data.ChangelogReleaseItem
import com.michaelflisar.composechangelog.data.XMLAttribute
import com.michaelflisar.composechangelog.data.XMLTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream

internal object ChangelogParserUtil {

    suspend fun parse(
        logFileReader: suspend () -> ByteArray,
        versionFormatter: ChangelogVersionFormatter,
    ): List<ChangelogReleaseItem> {
        return withContext(Dispatchers.IO) {
            val bytes = logFileReader()
            val inputStream = bytes.inputStream()
            val parser = Xml.newPullParser()
            parser.setInput(inputStream, Charsets.UTF_8.name())

            // read all <release> tags
            val items = ArrayList<ChangelogReleaseItem>()
            items.addAll(parseMainNode(parser, versionFormatter))
            items
        }
    }

    private fun children(innerText: String): List<XMLTag> {
        val parser = rawStringToParser(innerText, wrapInRoot = true)
        val items = ArrayList<XMLTag>()

        // Zum <root> Tag springen
        while (parser.eventType != XmlPullParser.START_TAG) {
            parser.next()
        }
        val rootDepth = parser.depth

        // Jetzt alle direkten Kind-Tags von <root> sammeln
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG && parser.depth == rootDepth + 1) {
                val tag = parser.name
                val attributes = parser.getXMLAttributes()
                val inner = parser.getInnerXml()
                val children = children(inner)
                items.add(XMLTag(tag, attributes, inner, children))
            } else if (parser.eventType == XmlPullParser.END_TAG && parser.depth == rootDepth) {
                break
            }
        }
        return items
    }

    @Throws(Exception::class)
    private fun parseMainNode(
        parser: XmlPullParser,
        versionFormatter: ChangelogVersionFormatter,
    ): List<ChangelogReleaseItem> {
        val items = ArrayList<ChangelogReleaseItem>()
        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                if (parser.name == Constants.XML_RELEASE_TAG) {
                    items.add(readReleaseNode(parser, versionFormatter))
                }
            }
            parser.next()
        }
        return items
    }

    @Throws(Exception::class)
    private fun readReleaseNode(
        parser: XmlPullParser,
        versionFormatter: ChangelogVersionFormatter,
    ): ChangelogReleaseItem {

        // 1) real all attributes of release tag
        parser.require(XmlPullParser.START_TAG, null, Constants.XML_RELEASE_TAG)
        val attrVersionName = parser.getAttributeValue(null, Constants.XML_ATTR_VERSION_NAME)
        val attrVersionCode = parser.getAttributeValue(null, Constants.XML_ATTR_VERSION_CODE)
        val versionCode: Int
        val versionName: String
        if (attrVersionName != null && attrVersionCode != null) {
            throw RuntimeException("Please only provide ${Constants.XML_ATTR_VERSION_NAME} OR ${Constants.XML_ATTR_VERSION_CODE}!")
        } else if (attrVersionName != null) {
            versionCode = versionFormatter.parseVersion(attrVersionName)
            versionName = attrVersionName
        } else {
            versionCode = attrVersionCode.toInt()
            versionName = versionFormatter.formatVersion(versionCode)
        }
        val attrTitle = parser.getAttributeValue(null, Constants.XML_ATTR_TITLE)
        val attrDate = parser.getAttributeValue(null, Constants.XML_ATTR_DATE)

        // 2) Parse all nested tags in release
        val items = ArrayList<XMLTag>()
        val depth = parser.depth
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG && parser.depth == depth + 1) {
                val tag = parser.name
                val attributes = parser.getXMLAttributes()
                val innerText = parser.getInnerXml()
                val children = children(innerText)
                items.add(XMLTag(tag, attributes, innerText, children))
            } else if (parser.eventType == XmlPullParser.END_TAG && parser.depth == depth) {
                break
            }
        }

        // 3) Create release element and add it to changelog object
        val release = ChangelogReleaseItem(
            versionCode,
            versionName,
            attrDate,
            attrTitle,
            items
        )

        return release
    }

    private fun XmlPullParser.getInnerXml(): String {
        val result = StringBuilder()
        var depth = 1

        while (depth != 0) {
            when (next()) {
                XmlPullParser.START_TAG -> {
                    result.append("<${name}>")
                    // Optional: auch Attribute einfügen, falls nötig
                    depth++
                }

                XmlPullParser.TEXT -> result.append(text)
                XmlPullParser.END_TAG -> {
                    depth--
                    if (depth > 0)
                        result.append("</${name}>")
                }
            }
        }

        return result.toString()
    }

    private fun XmlPullParser.getXMLAttributes(): List<XMLAttribute> {
        val list = ArrayList<XMLAttribute>()
        for (i in 0 until attributeCount) {
            val name = getAttributeName(i)
            val value = getAttributeValue(i)
            list.add(XMLAttribute(name, value))
        }
        return list
    }

    private fun rawStringToParser(rawXml: String, wrapInRoot: Boolean): XmlPullParser {
        val text = if (wrapInRoot) {
            "<root>$rawXml</root>"
        } else {
            rawXml
        }
        val parser = Xml.newPullParser()
        val inputStream = ByteArrayInputStream(text.toByteArray(Charsets.UTF_8))
        parser.setInput(inputStream, Charsets.UTF_8.name())
        return parser
    }
}