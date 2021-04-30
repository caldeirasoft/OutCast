package com.caldeirasoft.outcast.data.network.rss

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl
import com.caldeirasoft.outcast.domain.common.ParserConst.CATEGORY
import com.caldeirasoft.outcast.domain.common.ParserConst.CHANNEL
import com.caldeirasoft.outcast.domain.common.ParserConst.CLOUD
import com.caldeirasoft.outcast.domain.common.ParserConst.DAY
import com.caldeirasoft.outcast.domain.common.ParserConst.DESCRIPTION
import com.caldeirasoft.outcast.domain.common.ParserConst.DOMAIN
import com.caldeirasoft.outcast.domain.common.ParserConst.ENCLOSURE
import com.caldeirasoft.outcast.domain.common.ParserConst.GOOGLE_OWNER
import com.caldeirasoft.outcast.domain.common.ParserConst.GUID
import com.caldeirasoft.outcast.domain.common.ParserConst.HEIGHT
import com.caldeirasoft.outcast.domain.common.ParserConst.HOUR
import com.caldeirasoft.outcast.domain.common.ParserConst.IMAGE
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_EMAIL
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_NAME
import com.caldeirasoft.outcast.domain.common.ParserConst.ITUNES_OWNER
import com.caldeirasoft.outcast.domain.common.ParserConst.LENGTH
import com.caldeirasoft.outcast.domain.common.ParserConst.LINK
import com.caldeirasoft.outcast.domain.common.ParserConst.NAME
import com.caldeirasoft.outcast.domain.common.ParserConst.PATH
import com.caldeirasoft.outcast.domain.common.ParserConst.PERMALINK
import com.caldeirasoft.outcast.domain.common.ParserConst.PORT
import com.caldeirasoft.outcast.domain.common.ParserConst.PROTOCOL
import com.caldeirasoft.outcast.domain.common.ParserConst.REGISTER_PROCEDURE
import com.caldeirasoft.outcast.domain.common.ParserConst.SKIP_DAYS
import com.caldeirasoft.outcast.domain.common.ParserConst.SKIP_HOURS
import com.caldeirasoft.outcast.domain.common.ParserConst.SOURCE
import com.caldeirasoft.outcast.domain.common.ParserConst.TEXT
import com.caldeirasoft.outcast.domain.common.ParserConst.TEXT_INPUT
import com.caldeirasoft.outcast.domain.common.ParserConst.TITLE
import com.caldeirasoft.outcast.domain.common.ParserConst.TYPE
import com.caldeirasoft.outcast.domain.common.ParserConst.URL
import com.caldeirasoft.outcast.domain.common.ParserConst.WIDTH
import com.caldeirasoft.outcast.domain.models.rss.channel.*
import com.caldeirasoft.outcast.domain.models.rss.item.Category
import com.caldeirasoft.outcast.domain.models.rss.item.Enclosure
import com.caldeirasoft.outcast.domain.models.rss.item.Guid
import com.caldeirasoft.outcast.domain.models.rss.item.Source
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

abstract class ParserBase<out T : RssStandardChannel> : Parser<T> {

    protected inline fun <T> parseChannel(xml: String, action: Element.() -> T): T {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val document = builder.parse(xml.byteInputStream())
        document.documentElement.normalize()
        val nodeList = document.getElementsByTagName(CHANNEL)
        var result: T? = null

        // It can only have a channel.
        if (nodeList?.length == 1) {
            val element = nodeList.item(0) as? Element
            element?.let {
                result = action(it)
            }
        }
        return result ?: throw IllegalArgumentException("No valid channel tag in the RSS feed.")
    }

    protected fun Element.readImage(): Image? {
        val element = getElementByTag(IMAGE) ?: return null

        val link = element.readString(LINK)
        val title = element.readString(TITLE)
        val url = element.readString(URL)
        val description = element.readString(DESCRIPTION)
        val height = element.readString(HEIGHT)?.toIntOrNull()
        val width = element.readString(WIDTH)?.toIntOrNull()
        return Image(
            link = link,
            title = title,
            url = url,
            description = description,
            height = height,
            width = width
        )
    }

    protected fun Element.readCategories(parentTag: String): List<Category> {
        val result = mutableListOf<Category>()
        val nodeList = getElementsByTagName(CATEGORY) ?: return result

        for (i in 0 until nodeList.length) {
            val e = nodeList.item(i) as? Element ?: continue
            val parent = e.parentNode as? DeferredElementImpl
            if (parent?.tagName != parentTag) continue

            val domain: String? = e.getAttributeOrNull(DOMAIN)
            val name: String? = e.textContent?.takeIf { it.isNotEmpty() }
            result.add(Category(name = name, domain = domain))
        }
        return result
    }

    protected fun Element.readCategories(parentTag: String, tagName: String): List<Category>? {
        val result = mutableListOf<Category>()
        val nodeList = getElementsByTagName(tagName) ?: return null

        for (i in 0 until nodeList.length) {
            val e = nodeList.item(i) as? Element ?: continue
            val parent = e.parentNode as? DeferredElementImpl
            if (parent?.tagName == parentTag || parent?.tagName == tagName) {
                result.add(Category(name = e.getAttributeOrNull(TEXT), domain = null))
            }
        }
        return result
    }

    protected fun Element.readITunesOwner(): Owner? {
        val nodeList = getElementsByTagName(ITUNES_OWNER) ?: return null
        if (nodeList.length == 0) return null

        val element = getElementByTag(ITUNES_OWNER)
        val name = element?.readString(ITUNES_NAME)
        val email = element?.readString(ITUNES_EMAIL)
        return Owner(name = name, email = email)
    }

    protected fun Element.readGoogleOwner(): Owner? {
        val nodeList = getElementsByTagName(GOOGLE_OWNER)
        if (nodeList.length == 0) return null

        return Owner(name = null, email = readString(GOOGLE_OWNER))
    }

    protected fun Element.readCloud(): Cloud? {
        val element = getElementByTag(CLOUD) ?: return null

        val domain: String? = element.getAttributeOrNull(DOMAIN)
        val port: Int? = element.getAttributeOrNull(PORT)?.toIntOrNull()
        val path: String? = element.getAttributeOrNull(PATH)
        val registerProcedure: String? = element.getAttributeOrNull(REGISTER_PROCEDURE)
        val protocol: String? = element.getAttributeOrNull(PROTOCOL)

        return Cloud(
            domain = domain,
            port = port,
            path = path,
            registerProcedure = registerProcedure,
            protocol = protocol
        )
    }

    protected fun Element.readTextInput(): TextInput? {
        val element = getElementByTag(TEXT_INPUT) ?: return null

        val title: String? = element.readString(TITLE)
        val description: String? = element.readString(name = DESCRIPTION, parentTag = TEXT_INPUT)
        val name: String? = element.readString(NAME)
        val link: String? = element.readString(LINK)
        return TextInput(title = title, description = description, name = name, link = link)
    }

    protected fun Element.readSkipHours(): List<Int>? {
        val element = getElementByTag(SKIP_HOURS) ?: return null

        val hours = mutableListOf<Int>()
        val nodes = element.getElementsByTagName(HOUR)
        for (i in 0 until nodes.length) {
            val e = nodes.item(i) as? Element
            e?.textContent?.toIntOrNull()?.let { hours.add(it) }
        }
        return hours
    }

    protected fun Element.readSkipDays(): List<String>? {
        val element = getElementByTag(SKIP_DAYS) ?: return null

        val days = mutableListOf<String>()
        val nodes = element.getElementsByTagName(DAY)
        for (i in 0 until nodes.length) {
            val e = nodes.item(i) as? Element
            e?.textContent?.let { days.add(it) }
        }
        return days
    }

    protected fun Element.readEnclosure(): Enclosure? {
        val element = getElementByTag(ENCLOSURE) ?: return null

        val url = element.getAttributeOrNull(URL)
        val length = element.getAttributeOrNull(LENGTH)?.toLongOrNull()
        val type = element.getAttributeOrNull(TYPE)
        return Enclosure(url = url, length = length, type = type)
    }

    protected fun Element.readGuid(): Guid? {
        val element = getElementByTag(GUID) ?: return null

        val isPermaLink = element.getAttributeOrNull(PERMALINK)?.toBoolean()
        val value = readString(GUID)
        return Guid(value = value, isPermaLink = isPermaLink)
    }

    protected fun Element.readSource(): Source? {
        val element = getElementByTag(SOURCE) ?: return null

        val url = element.getAttributeOrNull(URL)
        val title = readString(SOURCE)
        return Source(title = title, url = url)
    }

    protected fun Element.readString(name: String, parentTag: String? = null): String? {
        val nodeList = getElementsByTagName(name)
        if (parentTag == null) {
            return nodeList.item(0)?.textContent
        } else {
            for (i in 0 until nodeList.length) {
                val e = nodeList.item(i) as? Element ?: continue
                val parent = e.parentNode as? DeferredElementImpl
                if (parent?.tagName != parentTag) continue

                return e.textContent
            }
            return null
        }
    }

    protected fun Element.getAttributeOrNull(tag: String): String? {
        val attr = getAttribute(tag) ?: return null
        return if (attr.isEmpty() || attr.isBlank()) null else attr
    }

    protected fun Element.getElementByTag(tag: String): Element? {
        val nodeList = getElementsByTagName(tag)
        if (nodeList.length == 0) return null
        return nodeList.item(0) as? Element
    }

    protected fun String.toBoolOrNull(): Boolean? {
        return when (toLowerCase()) {
            "yes", "true" -> true
            "no", "false" -> false
            else -> null
        }
    }
}