package org.igor.urly.application.services

import org.igor.urly.domain.models.LongUrl
import org.igor.urly.domain.models.ShortUrl
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils
import kotlin.random.Random.Default.nextBytes

interface UrlShortenerService {
    fun shortIt(url: LongUrl): ShortUrl
}

@Service
class Md5UrlShortenerService(
    @Value("\${urly.shortener.md5.size}") private val size: Int,
) : UrlShortenerService {
    override fun shortIt(url: LongUrl): ShortUrl {
        val salt = nextBytes(16)
        return DigestUtils
            .md5DigestAsHex(url.value.toByteArray() + salt)
            .take(size)
            .let(::ShortUrl)
    }
}
