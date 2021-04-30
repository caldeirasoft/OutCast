package com.caldeirasoft.outcast.data.network.rss

import com.caldeirasoft.outcast.domain.common.ParserConst.AUTHOR
import com.caldeirasoft.outcast.domain.common.ParserConst.CHANNEL
import com.caldeirasoft.outcast.domain.common.ParserConst.COMMENTS
import com.caldeirasoft.outcast.domain.common.ParserConst.COPYRIGHT
import com.caldeirasoft.outcast.domain.common.ParserConst.DESCRIPTION
import com.caldeirasoft.outcast.domain.common.ParserConst.DOCS
import com.caldeirasoft.outcast.domain.common.ParserConst.GENERATOR
import com.caldeirasoft.outcast.domain.common.ParserConst.ITEM
import com.caldeirasoft.outcast.domain.common.ParserConst.LANGUAGE
import com.caldeirasoft.outcast.domain.common.ParserConst.LAST_BUILD_DATE
import com.caldeirasoft.outcast.domain.common.ParserConst.LINK
import com.caldeirasoft.outcast.domain.common.ParserConst.MANAGING_EDITOR
import com.caldeirasoft.outcast.domain.common.ParserConst.PUB_DATE
import com.caldeirasoft.outcast.domain.common.ParserConst.RATING
import com.caldeirasoft.outcast.domain.common.ParserConst.TITLE
import com.caldeirasoft.outcast.domain.common.ParserConst.TTL
import com.caldeirasoft.outcast.domain.common.ParserConst.WEB_MASTER
import com.caldeirasoft.outcast.domain.models.rss.channel.RssStandardChannel
import com.caldeirasoft.outcast.domain.models.rss.channel.RssStandardChannelData
import com.caldeirasoft.outcast.domain.models.rss.item.RssStandardItemData
import org.w3c.dom.Element

class RssStandardParser : ParserBase<RssStandardChannel>() {

    override fun parse(xml: String): RssStandardChannel {
        return parseChannel(xml) {
            val title = readString(TITLE)
            val description = readString(name = DESCRIPTION, parentTag = CHANNEL)
            val link = readString(LINK)
            val image = readImage()
            val language = readString(LANGUAGE)
            val categories = readCategories(CHANNEL) ?: listOf()
            val copyright = readString(COPYRIGHT)
            val managingEditor = readString(MANAGING_EDITOR)
            val webMaster = readString(WEB_MASTER)
            val pubDate = readString(PUB_DATE)
            val lastBuildDate = readString(LAST_BUILD_DATE)
            val generator = readString(GENERATOR)
            val docs = readString(DOCS)
            val cloud = readCloud()
            val ttl = readString(TTL)?.toIntOrNull()
            val rating = readString(RATING)
            val textInput = readTextInput()
            val skipHours = readSkipHours()
            val skipDays = readSkipDays()
            val items = readItems()

            RssStandardChannelData(
                title = title,
                description = description,
                image = image,
                language = language,
                categories = if (categories.isEmpty()) null else categories,
                link = link,
                copyright = copyright,
                managingEditor = managingEditor,
                webMaster = webMaster,
                pubDate = pubDate,
                lastBuildDate = lastBuildDate,
                generator = generator,
                docs = docs,
                cloud = cloud,
                ttl = ttl,
                rating = rating,
                textInput = textInput,
                skipHours = skipHours,
                skipDays = skipDays,
                items = if (items.isEmpty()) null else items
            )
        }
    }

    private fun Element.readItems(): List<RssStandardItemData> {
        val result = mutableListOf<RssStandardItemData>()
        val nodeList = getElementsByTagName(ITEM) ?: return result

        for (i in 0 until nodeList.length) {
            val element = nodeList.item(i) as? Element ?: continue

            val title = element.readString(TITLE)
            val enclosure = element.readEnclosure()
            val guid = element.readGuid()
            val pubDate = element.readString(PUB_DATE)
            val description = element.readString(DESCRIPTION)
            val link = element.readString(LINK)
            val author = element.readString(AUTHOR)
            val categories = element.readCategories(ITEM)
            val comments = element.readString(COMMENTS)
            val source = element.readSource()
            result.add(
                RssStandardItemData(
                    title = title,
                    enclosure = enclosure,
                    guid = guid,
                    pubDate = pubDate,
                    description = description,
                    link = link,
                    author = author,
                    categories = if (categories.isEmpty()) null else categories,
                    comments = comments,
                    source = source
                )
            )
        }
        return result
    }
}