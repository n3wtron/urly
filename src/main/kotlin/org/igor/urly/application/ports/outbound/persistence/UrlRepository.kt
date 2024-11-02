package org.igor.urly.application.ports.outbound.persistence

import org.igor.urly.domain.entities.NewUrlEntity
import org.igor.urly.domain.entities.UrlEntity
import org.igor.urly.domain.models.ShortUrl

interface UrlRepository {
    fun save(url: NewUrlEntity): UrlEntity

    fun get(shortUrl: ShortUrl): UrlEntity?
}
