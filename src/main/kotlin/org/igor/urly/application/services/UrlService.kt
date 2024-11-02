package org.igor.urly.application.services

import org.igor.urly.application.ports.outbound.persistence.UrlRepository
import org.igor.urly.domain.entities.NewUrlEntity
import org.igor.urly.domain.entities.UrlEntity
import org.igor.urly.domain.models.LongUrl
import org.igor.urly.domain.models.ShortUrl
import org.springframework.stereotype.Service

interface UrlService {
    fun save(url: LongUrl): UrlEntity

    fun get(url: ShortUrl): UrlEntity?
}

@Service
class UrlServiceImpl(
    private val urlRepository: UrlRepository,
    private val urlShortenerService: UrlShortenerService,
) : UrlService {
    override fun save(url: LongUrl): UrlEntity =
        urlShortenerService
            .shortIt(url)
            .let { urlRepository.save(NewUrlEntity(it, url)) }

    override fun get(url: ShortUrl): UrlEntity? = urlRepository.get(url)
}
