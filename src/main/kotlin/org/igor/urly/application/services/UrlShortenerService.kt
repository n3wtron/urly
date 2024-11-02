package org.igor.urly.application.services

import org.igor.urly.domain.models.LongUrl
import org.igor.urly.domain.models.ShortUrl
import org.springframework.stereotype.Service

interface UrlShortenerService {
    fun shortIt(url: LongUrl): ShortUrl
}

@Service
class UrlShortenerServiceImpl : UrlShortenerService {
    // FIXME: just a fake implementation
    override fun shortIt(url: LongUrl): ShortUrl = ShortUrl(url.value.take(10))
}
