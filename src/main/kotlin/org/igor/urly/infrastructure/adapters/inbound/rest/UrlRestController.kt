package org.igor.urly.infrastructure.adapters.inbound.rest

import org.igor.urly.application.services.UrlService
import org.igor.urly.domain.models.LongUrl
import org.igor.urly.domain.models.ShortUrl
import org.igor.urly.infrastructure.adapters.inbound.rest.requests.NewUrlRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/urls")
class UrlRestController(
    private val urlService: UrlService,
) {
    @PostMapping(produces = [MediaType.TEXT_PLAIN_VALUE])
    fun create(
        @RequestBody newUrlRequest: NewUrlRequest,
    ) = urlService
        .save(LongUrl(newUrlRequest.url))
        .shortUrl
        .value

    @GetMapping(produces = [MediaType.TEXT_PLAIN_VALUE])
    fun get(
        @RequestParam url: String,
    ): ResponseEntity<String> =
        urlService
            .get(ShortUrl(url))
            ?.let { ResponseEntity.ok(it.longUrl.value) }
            ?: ResponseEntity.notFound().build()
}
