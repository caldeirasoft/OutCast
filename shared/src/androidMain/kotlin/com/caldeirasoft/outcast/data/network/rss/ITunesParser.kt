package com.caldeirasoft.outcast.data.network.rss

import com.caldeirasoft.outcast.data.utils.logD
import com.caldeirasoft.outcast.domain.models.rss.channel.ITunesChannelData
import com.caldeirasoft.outcast.domain.models.rss.channel.RssStandardChannelData
import com.caldeirasoft.outcast.domain.common.ParserConst.CHANNEL
import com.caldeirasoft.outcast.domain.common.ParserConst.COMMENTS
import com.caldeirasoft.outcast.domain.common.ParserConst.COPYRIGHT
import com.caldeirasoft.outcast.domain.common.ParserConst.DESCRIPTION
import com.caldeirasoft.outcast.domain.common.ParserConst.DOCS
import com.caldeirasoft.outcast.domain.common.ParserConst.GENERATOR
import com.caldeirasoft.outcast.domain.common.ParserConst.HREF
import com.caldeirasoft.outcast.domain.common.ParserConst.ITEM
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_AUTHOR
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_BLOCK
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_CATEGORY
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_COMPLETE
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_DURATION
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_EMAIL
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_EPISODE
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_EPISODE_TYPE
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_EXPLICIT
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_IMAGE
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_KEYWORDS
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_NAME
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_NEW_FEED_URL
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_OWNER
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_SEASON
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_SUBTITLE
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_SUMMARY
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_TITLE
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_TYPE
import com.caldeirasoft.outcast.domain.common.ParserConst.LANGUAGE
import com.caldeirasoft.outcast.domain.common.ParserConst.LAST_BUILD_DATE
import com.caldeirasoft.outcast.domain.common.ParserConst.LINK
import com.caldeirasoft.outcast.domain.common.ParserConst.MANAGING_EDITOR
import com.caldeirasoft.outcast.domain.common.ParserConst.PUB_DATE
import com.caldeirasoft.outcast.domain.common.ParserConst.RATING
import com.caldeirasoft.outcast.domain.common.ParserConst.TEXT
import com.caldeirasoft.outcast.domain.common.ParserConst.TITLE
import com.caldeirasoft.outcast.domain.common.ParserConst.TTL
import com.caldeirasoft.outcast.domain.common.ParserConst.WEB_MASTER
import com.caldeirasoft.outcast.domain.models.rss.channel.Image
import com.caldeirasoft.outcast.domain.models.rss.channel.Owner
import com.caldeirasoft.outcast.domain.models.rss.item.Category
import com.caldeirasoft.outcast.domain.models.rss.item.ITunesItemData
import com.caldeirasoft.outcast.domain.models.rss.item.RssStandardItem
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

class ITunesParser : ParserBase<ITunesChannelData>() {

    override val logTag: String = ITunesParser::class.java.simpleName

    override fun parse(xml: String) = parseITunesChannel(xml)

    private fun parseITunesChannel(xml: String): ITunesChannelData {
        val standardChannel = parseStandardChannel(xml)
        return parseChannel(xml) { readITunesChannel(standardChannel) }
    }

    private fun XmlPullParser.readITunesChannel(standardChannel: RssStandardChannelData): ITunesChannelData {
        require(XmlPullParser.START_TAG, null, CHANNEL)
        logD(logTag, "[readITunesChannel]: Reading iTunes channel")
        var image: Image? = null
        var explicit: Boolean? = null
        var categories: List<Category>? = null
        var author: String? = null
        var owner: Owner? = null
        var simpleTitle: String? = null
        var type: String? = null
        var newFeedUrl: String? = null
        var block: Boolean? = null
        var complete: Boolean? = null
        val items: MutableList<ITunesItemData> = mutableListOf()
        var itemIndex = 0

        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                ITUNES_IMAGE -> image = readImage()
                ITUNES_EXPLICIT -> explicit = readString(ITUNES_EXPLICIT)?.toBoolean()
                ITUNES_CATEGORY -> categories = readCategory()
                ITUNES_AUTHOR -> author = readString(ITUNES_AUTHOR)
                ITUNES_OWNER -> owner = readOwner()
                ITUNES_TITLE -> simpleTitle = readString(ITUNES_TITLE)
                ITUNES_TYPE -> type = readString(ITUNES_TYPE)
                ITUNES_NEW_FEED_URL -> newFeedUrl = readString(ITUNES_NEW_FEED_URL)
                ITUNES_BLOCK -> block = readString(ITUNES_BLOCK)?.toBoolOrNull()
                ITUNES_COMPLETE -> complete = readString(ITUNES_COMPLETE)?.toBoolOrNull()
                ITEM -> {
                    standardChannel.items?.get(itemIndex)?.let {
                        items.add(readItem(it))
                        itemIndex++
                    }
                }
                else -> skip()
            }
        }

        require(XmlPullParser.END_TAG, null, CHANNEL)
        return ITunesChannelData(
            title = standardChannel.title,
            description = standardChannel.description,
            image = image,
            language = standardChannel.language,
            categories = categories?.takeIf { it.isNotEmpty() },
            link = standardChannel.link,
            copyright = standardChannel.copyright,
            managingEditor = standardChannel.managingEditor,
            webMaster = standardChannel.webMaster,
            pubDate = standardChannel.pubDate,
            lastBuildDate = standardChannel.lastBuildDate,
            generator = standardChannel.generator,
            docs = standardChannel.docs,
            cloud = standardChannel.cloud,
            ttl = standardChannel.ttl,
            rating = standardChannel.rating,
            textInput = standardChannel.textInput,
            skipHours = standardChannel.skipHours,
            skipDays = standardChannel.skipDays,
            items = items.takeIf { it.isNotEmpty() },
            simpleTitle = simpleTitle,
            explicit = explicit,
            author = author,
            owner = owner,
            type = type,
            newFeedUrl = newFeedUrl,
            block = block,
            complete = complete
        )
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readImage(): Image {
        require(XmlPullParser.START_TAG, null, ITUNES_IMAGE)
        val href: String? = getAttributeValue(null, HREF)
        nextTag()
        require(XmlPullParser.END_TAG, null, ITUNES_IMAGE)
        logD(logTag, "[readImage]: href = $href")
        return Image(
            link = null,
            title = null,
            url = href,
            description = null,
            height = null,
            width = null
        )
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readCategory(): List<Category>? {
        require(XmlPullParser.START_TAG, null, ITUNES_CATEGORY)
        val categories = mutableListOf<Category>()
        getAttributeValue(null, TEXT)?.let { categories.add(Category(name = it, domain = null)) }
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                ITUNES_CATEGORY -> {
                    getAttributeValue(null, TEXT)
                        ?.let { categories.add(Category(name = it, domain = null)) }
                    nextTag()
                }
                else -> skip()
            }
        }
        require(XmlPullParser.END_TAG, null, ITUNES_CATEGORY)
        logD(logTag, "[readCategory]: categories = $categories")
        return categories
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readOwner(): Owner {
        require(XmlPullParser.START_TAG, null, ITUNES_OWNER)
        var name: String? = null
        var email: String? = null
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (this.name) {
                ITUNES_NAME -> name = readString(ITUNES_NAME)
                ITUNES_EMAIL -> email = readString(ITUNES_EMAIL)
                else -> skip()
            }
        }
        require(XmlPullParser.END_TAG, null, ITUNES_OWNER)
        logD(logTag, "[readOwner] name = $name, email = $email")
        return Owner(name = name, email = email)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun XmlPullParser.readItem(standardItem: RssStandardItem): ITunesItemData {
        require(XmlPullParser.START_TAG, null, ITEM)
        logD(logTag, "[readItem]: Reading iTunes item")
        var simpleTitle: String? = null
        var duration: String? = null
        var image: String? = null
        var explicit: Boolean? = null
        var episode: Int? = null
        var season: Int? = null
        var episodeType: String? = null
        var block: Boolean? = null
        var author: String? = null
        var summary: String? = null
        var subtitle: String? = null
        var keywords: String? = null
        while (next() != XmlPullParser.END_TAG) {
            if (eventType != XmlPullParser.START_TAG) continue

            when (name) {
                ITUNES_DURATION -> duration = readString(ITUNES_DURATION)
                ITUNES_IMAGE -> image = readImage().url
                ITUNES_EXPLICIT -> explicit = readString(ITUNES_EXPLICIT)?.toBoolean()
                ITUNES_TITLE -> simpleTitle = readString(ITUNES_TITLE)
                ITUNES_EPISODE -> episode = readString(ITUNES_EPISODE)?.toIntOrNull()
                ITUNES_SEASON -> season = readString(ITUNES_SEASON)?.toIntOrNull()
                ITUNES_EPISODE_TYPE -> episodeType = readString(ITUNES_EPISODE_TYPE)
                ITUNES_BLOCK -> block = readString(ITUNES_BLOCK)?.toBoolOrNull()
                ITUNES_AUTHOR -> author = readString(ITUNES_AUTHOR)
                ITUNES_SUMMARY -> summary = readString(ITUNES_SUMMARY)
                ITUNES_SUBTITLE -> subtitle = readString(ITUNES_SUBTITLE)
                ITUNES_KEYWORDS -> keywords = readString(ITUNES_KEYWORDS)
                else -> skip()
            }
        }

        require(XmlPullParser.END_TAG, null, ITEM)
        return ITunesItemData(
            title = standardItem.title,
            enclosure = standardItem.enclosure,
            guid = standardItem.guid,
            pubDate = standardItem.pubDate,
            description = standardItem.description,
            link = standardItem.link,
            author = author,
            categories = standardItem.categories,
            comments = standardItem.comments,
            source = standardItem.source,
            simpleTitle = simpleTitle,
            duration = duration,
            image = image,
            explicit = explicit,
            episode = episode,
            season = season,
            episodeType = episodeType,
            block = block,
            summary = summary,
            subtitle = subtitle,
            keywords = keywords,
        )
    }
}