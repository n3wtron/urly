package org.igor.urly.application.services

import org.igor.urly.application.ports.outbound.persistence.DuplicateUrlException
import org.igor.urly.application.ports.outbound.persistence.UrlRepository
import org.igor.urly.domain.entities.NewUrlEntity
import org.igor.urly.domain.entities.UrlEntity
import org.igor.urly.domain.models.LongUrl
import org.igor.urly.domain.models.ShortUrl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

interface UrlService {
    fun save(longUrl: LongUrl): UrlEntity

    fun get(shortUrl: ShortUrl): UrlEntity?
}

@Service
class UrlServiceImpl(
    private val urlRepository: UrlRepository,
    private val urlShortenerService: UrlShortenerService,
    @Value("\${urly.shortener.max-collision-attempts:10}")
    private val maxCollisionAttempts: Int = 10,
) : UrlService {
    private val logger = LoggerFactory.getLogger(UrlServiceImpl::class.java)

    override fun save(longUrl: LongUrl): UrlEntity {
        var attempts = 0
        do {
            val existentLongUrl = urlRepository.get(longUrl)
            if (existentLongUrl != null) return existentLongUrl
            try {
                val shortUrl = urlShortenerService.shortIt(longUrl)
                return urlRepository.save(NewUrlEntity(shortUrl, longUrl))
            } catch (e: DuplicateUrlException) {
                logger.warn("duplicate url: possible short url collision or already existent record", e)
                attempts++
            }
        } while (attempts < maxCollisionAttempts)
        error("Unable to save the url $longUrl, max attempts reached")
    }

    override fun get(shortUrl: ShortUrl): UrlEntity? = urlRepository.get(shortUrl)
}
