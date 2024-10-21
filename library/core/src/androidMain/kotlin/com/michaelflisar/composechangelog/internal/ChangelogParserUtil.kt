package com.michaelflisar.composechangelog.internal

import android.content.Context
import android.util.Log
import android.util.Xml
import com.michaelflisar.composechangelog.ChangelogVersionFormatter
import com.michaelflisar.composechangelog.classes.ChangelogData
import com.michaelflisar.composechangelog.classes.DataItemRelease
import com.michaelflisar.composechangelog.classes.DataItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

internal object ChangelogParserUtil {

    suspend fun parse(
        context: Context,
        resourceFile: Int,
        versionFormatter: ChangelogVersionFormatter,
        sorter: Comparator<DataItemRelease>? = null
    ): ChangelogData {
        return withContext(Dispatchers.IO) {
            try {
                val parser: XmlPullParser
                val resourceType: String = context.resources.getResourceTypeName(resourceFile)
                if (resourceType == "raw") {
                    val `in`: InputStream = context.resources.openRawResource(resourceFile)
                    parser = Xml.newPullParser()
                    parser.setInput(`in`, null)
                } else if (resourceType == "xml") {
                    parser = context.resources.getXml(resourceFile)
                } else throw RuntimeException("Wrong changelog resource type, provide xml or raw resource!")

                var id: Int = 1
                val idProvider = {
                    id++
                }

                // 1) Create Changelog items
                val items = ArrayList<DataItemRelease>()

                // 2) Parse file into Changelog object
                items.addAll(parseMainNode(parser, versionFormatter, idProvider))

                // 3) sort changelogs
                if (sorter != null) {
                    items.sortWith(sorter)
                }

                // 4) create Changelog object
                ChangelogData(items)
            } catch (xpe: XmlPullParserException) {
                Log.d(
                    Constants.DEBUG_TAG,
                    "XmlPullParseException while parsing changelog file",
                    xpe
                )
                throw xpe
            } catch (ioe: IOException) {
                Log.d(Constants.DEBUG_TAG, "IOException with changelog file", ioe)
                throw ioe
            }
        }
    }

    @Throws(Exception::class)
    private fun parseMainNode(
        parser: XmlPullParser,
        versionFormatter: ChangelogVersionFormatter,
        idProvider: () -> Int
    ): List<DataItemRelease> {

        val items = ArrayList<DataItemRelease>()

        // Parse all nested (=release) nodes
        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                val tag = parser.name
                if (tag == Constants.XML_RELEASE_TAG) {
                    items.addAll(readReleaseNode(parser, versionFormatter, idProvider))
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
        idProvider: () -> Int
    ): List<DataItemRelease> {

        val releases = ArrayList<DataItemRelease>()

        // 1) parse release tag
        parser.require(XmlPullParser.START_TAG, null, Constants.XML_RELEASE_TAG)

        // 2) real all attributes of release tag
        val versionNameXMLAttr = parser.getAttributeValue(null, Constants.XML_ATTR_VERSION_NAME)
        val versionCodeXMLAttr = parser.getAttributeValue(null, Constants.XML_ATTR_VERSION_CODE)
        val versionCode: Int
        val versionName: String
        if (versionNameXMLAttr != null && versionCodeXMLAttr != null) {
            throw RuntimeException("Please only provide ${Constants.XML_ATTR_VERSION_NAME} OR ${Constants.XML_ATTR_VERSION_CODE}!")
        } else if (versionNameXMLAttr != null) {
            versionCode = versionFormatter.parseVersion(versionNameXMLAttr)
            versionName = versionNameXMLAttr
        } else {
            versionCode = versionCodeXMLAttr.toInt()
            versionName = versionFormatter.formatVersion(versionCode)
        }

        val date = parser.getAttributeValue(null, Constants.XML_ATTR_DATE)
        val filter = parser.getAttributeValue(null, Constants.XML_ATTR_FILTER)

        // 3) Parse all nested tags in release
        val items = ArrayList<DataItem>()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val tag = parser.name
            items.add(readReleaseRowNode(tag, parser, idProvider))
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
        parser: XmlPullParser,
        idProvider: () -> Int
    ): DataItem {

        // 1) real all attributes of row tag
        val filter = parser.getAttributeValue(null, Constants.XML_ATTR_FILTER)
        val type = parser.getAttributeValue(null, Constants.XML_ATTR_TYPE)
        val isSummary = Constants.XML_VALUE_SUMMARY.equals(type, true)

        // 2) read content (=text) of row tag
        var text = ""
        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.text
            parser.nextTag()
        }

        // 3) create row element and add it to release element
        return DataItem(idProvider(), tag, text, filter, isSummary)
    }

}