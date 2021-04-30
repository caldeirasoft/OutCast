package com.caldeirasoft.outcast.data.network.rss

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
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_EPISODE
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_EPISODE_TYPE
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_EXPLICIT
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_IMAGE
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_KEYWORDS
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_NEW_FEED_URL
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
import com.caldeirasoft.outcast.domain.common.ParserConst.TITLE
import com.caldeirasoft.outcast.domain.common.ParserConst.TTL
import com.caldeirasoft.outcast.domain.common.ParserConst.WEB_MASTER
import com.caldeirasoft.outcast.domain.models.rss.channel.ITunesChannelData
import com.caldeirasoft.outcast.domain.models.rss.channel.Image
import com.caldeirasoft.outcast.domain.models.rss.channel.Owner
import com.caldeirasoft.outcast.domain.models.rss.item.Category
import com.caldeirasoft.outcast.domain.models.rss.item.ITunesItemData
import org.w3c.dom.Element

class ITunesParser : ParserBase<ITunesChannelData>() {

    override fun parse(xml: String): ITunesChannelData {
        return parseChannel(xml) {
            val title = readString(TITLE)
            val description = readString(name = DESCRIPTION, parentTag = CHANNEL)
            val link = readString(LINK)
            val language = readString(LANGUAGE)
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

            val image: Image? = readITunesImage()
            val explicit: Boolean? = readString(ITUNES_EXPLICIT)?.toBoolean()
            val categories: List<Category>? =
                readCategories(parentTag = CHANNEL, tagName = ITUNES_CATEGORY)
            val author: String? = readString(ITUNES_AUTHOR)
            val owner: Owner? = readITunesOwner()
            val simpleTitle: String? = readString(ITUNES_TITLE)
            val type: String? = readString(ITUNES_TYPE)
            val newFeedUrl: String? = readString(ITUNES_NEW_FEED_URL)
            val block: Boolean? = readString(ITUNES_COMPLETE)?.toBoolOrNull()
            val complete: Boolean? = readString(ITUNES_COMPLETE)?.toBoolOrNull()
            val items: List<ITunesItemData> = readItems()

            ITunesChannelData(
                title = title,
                description = description,
                image = image,
                language = language,
                categories = categories?.takeIf { it.isNotEmpty() },
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
                items = items.takeIf { it.isNotEmpty() },
                simpleTitle = simpleTitle,
                explicit = explicit,
                author = author,
                owner = owner,
                type = type,
                newFeedUrl = newFeedUrl,
                block = block,
                complete = complete,
            )
        }
    }

    private fun Element.readITunesImage(): Image? {
        val element = getElementByTag(ITUNES_IMAGE) ?: return null
        val href = element.getAttributeOrNull(HREF)
        return Image(
            link = null,
            title = null,
            url = href,
            description = null,
            height = null,
            width = null
        )
    }

    private fun Element.readItems(): List<ITunesItemData> {
        val result = mutableListOf<ITunesItemData>()
        val nodeList = getElementsByTagName(ITEM) ?: return result

        for (i in 0 until nodeList.length) {
            val element = nodeList.item(i) as? Element ?: continue

            val title = element.readString(TITLE)
            val enclosure = element.readEnclosure()
            val guid = element.readGuid()
            val pubDate = element.readString(PUB_DATE)
            val description = element.readString(DESCRIPTION)
            val link = element.readString(LINK)
            val categories = element.readCategories(ITEM)
            val comments = element.readString(COMMENTS)
            val source = element.readSource()

            val simpleTitle: String? = element.readString(ITUNES_TITLE)
            val duration: String? = element.readString(ITUNES_DURATION)
            val image: String? = element.getElementByTag(ITUNES_IMAGE)?.getAttributeOrNull(HREF)
            val explicit: Boolean? = element.readString(ITUNES_EXPLICIT)?.toBoolOrNull()
            val episode: Int? = element.readString(ITUNES_EPISODE)?.toIntOrNull()
            val season: Int? = element.readString(ITUNES_SEASON)?.toIntOrNull()
            val episodeType: String? = element.readString(ITUNES_EPISODE_TYPE)
            val block: Boolean? = element.readString(ITUNES_BLOCK)?.toBoolOrNull()
            val author: String? = element.readString(ITUNES_AUTHOR)
            val summary: String? = element.readString(ITUNES_SUMMARY)
            val subtitle: String? = element.readString(ITUNES_SUBTITLE)
            val keywords: String? = element.readString(ITUNES_KEYWORDS)

            result.add(
                ITunesItemData(
                    title = title,
                    enclosure = enclosure,
                    guid = guid,
                    pubDate = pubDate,
                    description = description,
                    link = link,
                    author = author,
                    categories = categories.takeIf { it.isNotEmpty() },
                    comments = comments,
                    source = source,
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
            )
        }
        return result
    }
}