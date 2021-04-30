package com.caldeirasoft.outcast.domain.models.rss.item

interface RssStandardItem {
    val title: String?
    val enclosure: Enclosure?
    val guid: Guid?
    val pubDate: String?
    val description: String?
    val link: String?
    val author: String?
    val categories: List<Category>?
    val comments: String?
    val source: Source?
}

data class RssStandardItemData(
    override val title: String?,
    override val enclosure: Enclosure?,
    override val guid: Guid?,
    override val pubDate: String?,
    override val description: String?,
    override val link: String?,
    override val author: String?,
    override val categories: List<Category>?,
    override val comments: String?,
    override val source: Source?,
) : RssStandardItem

interface ITunesItem: RssStandardItem {
    val simpleTitle: String?
    val duration: String?
    val image: String?
    val explicit: Boolean?
    val episode: Int?
    val season: Int?
    val episodeType: String?
    val block: Boolean?
    val summary: String?
    val subtitle: String?
    val keywords: String?
}

data class ITunesItemData(
    override val title: String?,
    override val enclosure: Enclosure?,
    override val guid: Guid?,
    override val pubDate: String?,
    override val description: String?,
    override val link: String?,
    override val author: String?,
    override val categories: List<Category>?,
    override val comments: String?,
    override val source: Source?,
    override val simpleTitle: String?,
    override val duration: String?,
    override val image: String?,
    override val explicit: Boolean?,
    override val episode: Int?,
    override val season: Int?,
    override val episodeType: String?,
    override val block: Boolean?,
    override val summary: String?,
    override val subtitle: String?,
    override val keywords: String?,
) : ITunesItem

interface GoogleItem : RssStandardItem {
    val explicit: Boolean?
    val block: Boolean?
}

data class GoogleItemData(
    override val title: String?,
    override val enclosure: Enclosure?,
    override val guid: Guid?,
    override val pubDate: String?,
    override val description: String?,
    override val link: String?,
    override val author: String?,
    override val categories: List<Category>?,
    override val comments: String?,
    override val source: Source?,
    override val explicit: Boolean?,
    override val block: Boolean?,
) : GoogleItem

interface AutoMixItem : ITunesItem, GoogleItem {
    override val simpleTitle: String?
    override val duration: String?
    override val image: String?
    override val explicit: Boolean?
    override val episode: Int?
    override val season: Int?
    override val episodeType: String?
    override val block: Boolean?
}

data class AutoMixItemData(
    override val title: String?,
    override val enclosure: Enclosure?,
    override val guid: Guid?,
    override val pubDate: String?,
    override val description: String?,
    override val link: String?,
    override val author: String?,
    override val categories: List<Category>?,
    override val comments: String?,
    override val source: Source?,
    override val simpleTitle: String?,
    override val duration: String?,
    override val image: String?,
    override val explicit: Boolean?,
    override val episode: Int?,
    override val season: Int?,
    override val episodeType: String?,
    override val block: Boolean?,
    override val summary: String?,
    override val subtitle: String?,
    override val keywords: String?,
) : AutoMixItem