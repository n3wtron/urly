package org.igor.urly.domain.entities

import org.igor.urly.domain.models.LongUrl
import org.igor.urly.domain.models.ShortUrl
import org.igor.urly.domain.models.UrlId

data class NewUrlEntity(
    val shortUrl: ShortUrl,
    val longUrl: LongUrl,
)

data class UrlEntity(
    val id: UrlId,
    val shortUrl: ShortUrl,
    val longUrl: LongUrl,
)
