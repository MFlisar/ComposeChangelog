package com.michaelflisar.composechangelog.internal

import com.michaelflisar.composechangelog.ChangelogVersionFormatter
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.DataItem
import com.michaelflisar.composechangelog.classes.DataItemRelease
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.io.IOException
import javax.script.ScriptEngine
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory

internal object ChangelogParserUtil {

    suspend fun parse(
        file: File,
        versionFormatter: ChangelogVersionFormatter,
        sorter: Comparator<DataItemRelease>? = null
    ): ChangelogData {
        return withContext(Dispatchers.IO) {
            try {
                val dbf = DocumentBuilderFactory.newInstance()
                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
                val db = dbf.newDocumentBuilder()
                val doc = db.parse(file)

                var id: Int = 1
                val idProvider = {
                    id++
                }

                // 1) Create Changelog items
                val items = ArrayList<DataItemRelease>()

                // 2) Parse file into Changelog object
                items.addAll(parseMainNode(doc, versionFormatter, idProvider))

                // 3) sort changelogs
                if (sorter != null) {
                    items.sortWith(sorter)
                }

                // 4) create Changelog object
                ChangelogData(items)
            } catch (xpe: Exception) {
                throw xpe
            } catch (ioe: IOException) {
                throw ioe
            }
        }
    }

    @Throws(Exception::class)
    private fun parseMainNode(
        doc: Document,
        versionFormatter: ChangelogVersionFormatter,
        idProvider: () -> Int
    ): List<DataItemRelease> {
        val items = ArrayList<DataItemRelease>()
        val nodesRelease = doc.getElementsByTagName(Constants.XML_RELEASE_TAG)
        for (i in 0 ..<nodesRelease.length) {
            val n = nodesRelease.item(i)
            if (n.nodeType == Node.ELEMENT_NODE) {
                items.addAll(readReleaseNode(n, versionFormatter, idProvider))
            }
        }
        return items
    }

    @Throws(Exception::class)
    private fun readReleaseNode(
        node: Node,
        versionFormatter: ChangelogVersionFormatter,
        idProvider: () -> Int
    ): List<DataItemRelease> {

        val releases = ArrayList<DataItemRelease>()

        val element = node as Element

        // 1) real all attributes of release tag
        val versionNameXMLAttr = if (element.hasAttribute(Constants.XML_ATTR_VERSION_NAME)) element.getAttribute(Constants.XML_ATTR_VERSION_NAME) else null
        val versionCodeXMLAttr = if (element.hasAttribute(Constants.XML_ATTR_VERSION_CODE)) element.getAttribute(Constants.XML_ATTR_VERSION_CODE) else null
        val versionCode: Int
        val versionName: String
        if (versionNameXMLAttr != null && versionCodeXMLAttr != null) {
            throw RuntimeException("Please only provide ${Constants.XML_ATTR_VERSION_NAME} OR ${Constants.XML_ATTR_VERSION_CODE}!")
        } else if (versionNameXMLAttr != null) {
            versionCode = versionFormatter.parseVersion(versionNameXMLAttr)
            versionName = versionNameXMLAttr
        } else {
            versionCode = versionCodeXMLAttr!!.toInt()
            versionName = versionFormatter.formatVersion(versionCode)
        }

        val date = element.getAttribute(Constants.XML_ATTR_DATE)
        val filter = if (element.hasAttribute(Constants.XML_ATTR_FILTER)) element.getAttribute(Constants.XML_ATTR_FILTER) else null

        // 3) Parse all nested tags in release
        val items = ArrayList<DataItem>()
        for (i in 0..<node.childNodes.length) {
            val n = node.childNodes.item(i)
            if (n.nodeType == Node.ELEMENT_NODE) {
                val tag = node.tagName
                items.add(readReleaseRowNode(tag, n, idProvider))
            }
        }

        // 4) Create release element and add it to changelog object
        val release = DataItemRelease(
            idProvider(),
            versionCode,
            versionName,
            date,
            filter,
            items
        )
        releases.add(release)

        return releases
    }

    @Throws(Exception::class)
    private fun readReleaseRowNode(
        tag: String,
        node: Node,
        idProvider: () -> Int
    ): DataItem {

        val element = node as Element

        // 1) real all attributes of row tag
        val filter = if (element.hasAttribute(Constants.XML_ATTR_FILTER)) element.getAttribute(Constants.XML_ATTR_FILTER) else null
        val type = if (element.hasAttribute(Constants.XML_ATTR_TYPE)) element.getAttribute(Constants.XML_ATTR_TYPE) else null
        val isSummary = Constants.XML_VALUE_SUMMARY.equals(type, true)

        val text = element.textContent

        // 3) create row element and add it to release element
        return DataItem(idProvider(), tag, text, filter, isSummary)
    }

}