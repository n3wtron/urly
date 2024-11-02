package org.igor.urly.infrastructure.adapters.outbound.persistence

import org.igor.urly.application.ports.outbound.persistence.UrlRepository
import org.igor.urly.domain.entities.NewUrlEntity
import org.igor.urly.domain.entities.UrlEntity
import org.igor.urly.domain.models.ShortUrl
import org.springframework.stereotype.Repository

@Repository
class JPAUrlRepository : UrlRepository {
    override fun save(url: NewUrlEntity): UrlEntity {
        TODO("Not yet implemented")
    }

    override fun get(shortUrl: ShortUrl): UrlEntity? {
        TODO("Not yet implemented")
    }
}
