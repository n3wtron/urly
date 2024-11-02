package org.igor.urly.application.ports.outbound.persistence

import org.igor.urly.domain.entities.NewUrlEntity
import org.igor.urly.domain.entities.UrlEntity
import org.igor.urly.domain.models.LongUrl
import org.igor.urly.domain.models.ShortUrl

class DuplicateUrlException(
    val longUrl: LongUrl,
    val shortUrl: ShortUrl,
) : IllegalStateException("URL already present")

interface UrlRepository {
    /**
     * @throws DuplicateUrlException
     */
    fun save(newUrl: NewUrlEntity): UrlEntity

    fun get(shortUrl: ShortUrl): UrlEntity?

    fun get(longUrl: LongUrl): UrlEntity?
}
