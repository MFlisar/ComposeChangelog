package com.michaelflisar.composechangelog.internal

import com.michaelflisar.composechangelog.ChangelogVersionFormatter
import com.michaelflisar.composechangelog.Constants
import com.michaelflisar.composechangelog.data.ChangelogReleaseItem
import com.michaelflisar.composechangelog.data.XMLAttribute
import com.michaelflisar.composechangelog.data.XMLTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.ByteArrayInputStream
import java.io.StringWriter
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

internal object ChangelogParserUtil {

    suspend fun parse(
        logFileReader: suspend () -> ByteArray,
        versionFormatter: ChangelogVersionFormatter,
    ): List<ChangelogReleaseItem> {
        return withContext(Dispatchers.IO) {

            val dbf = DocumentBuilderFactory.newInstance()
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
            val db = dbf.newDocumentBuilder()
            val bytes = logFileReader()
            val inputStream = bytes.inputStream()
            val doc = db.parse(inputStream)

            // read all <release> tags
            val items = ArrayList<ChangelogReleaseItem>()
            items.addAll(parseMainNode(doc, versionFormatter))
            items
        }
    }

    fun children(
        xmlTag: XMLTag,
    ): List<XMLTag> {
        val element = rawStringToElement(xmlTag.innerText, true)
        val items = ArrayList<XMLTag>()
        for (i in 0..<element.childNodes.length) {
            val n = element.childNodes.item(i)
            if (n.nodeType == Node.ELEMENT_NODE) {
                val element = n as Element
                items.add(
                    XMLTag(
                        element.nodeName,
                        element.getXMLAttributes(),
                        element.getInnerXml()
                    )
                )
            }
        }
        return items
    }

    @Throws(Exception::class)
    private fun parseMainNode(
        doc: Document,
        versionFormatter: ChangelogVersionFormatter,
    ): List<ChangelogReleaseItem> {
        val items = ArrayList<ChangelogReleaseItem>()
        val nodesRelease = doc.getElementsByTagName(Constants.XML_RELEASE_TAG)
        for (i in 0..<nodesRelease.length) {
            val n = nodesRelease.item(i)
            if (n.nodeType == Node.ELEMENT_NODE) {
                items.add(readReleaseNode(n as Element, versionFormatter))
            }
        }
        return items
    }

    @Throws(Exception::class)
    private fun readReleaseNode(
        element: Element,
        versionFormatter: ChangelogVersionFormatter,
    ): ChangelogReleaseItem {

        // 1) real all attributes of release tag
        val attrVersionName = element.getAttrOrNull(Constants.XML_ATTR_VERSION_NAME)
        val attrVersionCode = element.getAttrOrNull(Constants.XML_ATTR_VERSION_CODE)
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
        val attrTitle = element.getAttrOrNull(Constants.XML_ATTR_TITLE)
        val attrDate = element.getAttribute(Constants.XML_ATTR_DATE)

        // 2) Parse all nested tags in release
        val items = ArrayList<XMLTag>()
        for (i in 0..<element.childNodes.length) {
            val n = element.childNodes.item(i)
            if (n.nodeType == Node.ELEMENT_NODE) {
                val element2 = n as Element
                val tag = element2.tagName
                items.add(XMLTag(tag, element2.getXMLAttributes(), element2.getInnerXml()))
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

    private fun Element.getInnerXml(): String {
        val transformer = TransformerFactory.newInstance().newTransformer().apply {
            setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
            setOutputProperty(OutputKeys.INDENT, "no")
        }
        val innerXml = StringBuilder()
        for (i in 0 until childNodes.length) {
            val child: Node = childNodes.item(i)
            val writer = StringWriter()
            transformer.transform(DOMSource(child), StreamResult(writer))
            innerXml.append(writer.toString())
        }
        return innerXml.toString()
    }

    private fun Element.getXMLAttributes(): List<XMLAttribute> {
        val list = ArrayList<XMLAttribute>()
        for (i in 0 until attributes.length) {
            val attr = attributes.item(i)
            list.add(XMLAttribute(attr.nodeName, attr.nodeValue))
        }
        return list
    }

    private fun rawStringToElement(rawXml: String, wrapInRoot: Boolean): Element {
        val text = if (wrapInRoot) {
            "<root>$rawXml</root>"
        } else {
            rawXml
        }
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val inputStream = ByteArrayInputStream(text.toByteArray())
        val document = builder.parse(inputStream)
        return document.documentElement
    }

    private fun Element.getAttrOrNull(name: String) =
        if (hasAttribute(name)) getAttribute(name) else null
}