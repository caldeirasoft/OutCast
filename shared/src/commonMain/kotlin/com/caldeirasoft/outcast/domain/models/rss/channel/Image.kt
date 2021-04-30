package com.caldeirasoft.outcast.domain.models.rss.channel

data class Image(
    val link: String?,
    val title: String?,
    val url: String?,
    val description: String?,
    val height: Int?,
    val width: Int?,
) {
    companion object {
        fun Image?.replaceInvalidUrlByPriority(vararg priorityHref: String?): Image? {
            if (this == null || url != null) return this
            val href = priorityHref.firstOrNull { null != it } ?: return this

            return Image(
                link = link,
                title = title,
                url = href,
                description = description,
                height = height,
                width = width
            )
        }

        fun String?.hrefToImage(): Image? {
            this ?: return null

            return Image(
                link = null,
                title = null,
                url = this,
                description = null,
                height = null,
                width = null
            )
        }
    }
}