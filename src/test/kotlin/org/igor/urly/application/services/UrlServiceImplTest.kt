package org.igor.urly.application.services

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.igor.urly.application.ports.outbound.persistence.UrlRepository
import org.igor.urly.domain.entities.UrlEntity
import org.igor.urly.domain.models.LongUrl
import org.igor.urly.domain.models.ShortUrl
import org.igor.urly.domain.models.UrlId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.random.Random.Default.nextInt

class UrlServiceImplTest {
    private val urlRepository: UrlRepository = mockk()
    private val urlShortenerService: UrlShortenerService = mockk()
    private val urlService = UrlServiceImpl(urlRepository, urlShortenerService)

    @AfterEach
    fun tearDown() {
        confirmVerified(urlShortenerService, urlRepository)
    }

    @Test
    fun `should generate and persist a longUrl`() {
        // Given
        val longUrl = LongUrl("https://www.long.com")
        val expectedShortUrl = ShortUrl("shortUrl")
        val expectedUrlEntity = UrlEntity(randomUrlId(), expectedShortUrl, longUrl)

        every { urlShortenerService.shortIt(longUrl) } returns expectedShortUrl
        every { urlRepository.save(match { it.longUrl == longUrl && it.shortUrl == expectedShortUrl }) } returns
            expectedUrlEntity

        // When
        val result = urlService.save(longUrl)

        // Then
        expectThat(result).isEqualTo(expectedUrlEntity)
        verify(exactly = 1) {
            urlShortenerService.shortIt(longUrl)
            urlRepository.save(any())
        }
    }

    @Test
    fun `should retrieve a longUrl from a shortUrl`() {
        // Given
        val shortUrl = ShortUrl("abc")
        val expectedLongUrl = LongUrl("http://www.test.com")
        val expectedUrlEntity = UrlEntity(randomUrlId(), shortUrl, expectedLongUrl)
        every { urlRepository.get(shortUrl) } returns expectedUrlEntity

        // When
        val result = urlService.get(shortUrl)

        // Then
        expectThat(result).isEqualTo(expectedUrlEntity)
        verify(exactly = 1) { urlRepository.get(any()) }
    }

    private fun randomUrlId() = UrlId(nextInt(1, 100))
}
