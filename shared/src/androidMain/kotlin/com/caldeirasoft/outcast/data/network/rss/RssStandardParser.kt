package com.caldeirasoft.outcast.data.network.rss

import com.caldeirasoft.outcast.domain.models.rss.channel.RssStandardChannel
import org.xmlpull.v1.XmlPullParserException

class RssStandardParser : ParserBase<RssStandardChannel>() {

    override val logTag: String = RssStandardParser::class.java.simpleName

    @Throws(XmlPullParserException::class)
    override fun parse(xml: String) = parseStandardChannel(xml)
}