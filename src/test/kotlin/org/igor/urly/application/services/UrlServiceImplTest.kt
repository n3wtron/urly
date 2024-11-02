package org.igor.urly.application.services

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.igor.urly.application.ports.outbound.persistence.DuplicateUrlException
import org.igor.urly.application.ports.outbound.persistence.UrlRepository
import org.igor.urly.domain.entities.UrlEntity
import org.igor.urly.domain.models.LongUrl
import org.igor.urly.domain.models.ShortUrl
import org.igor.urly.domain.models.UrlId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import kotlin.random.Random.Default.nextInt

class UrlServiceImplTest {
    private val urlRepository: UrlRepository = mockk()
    private val urlShortenerService: UrlShortenerService = mockk()
    private val maxCollisionAttempts = 2
    private val urlService = UrlServiceImpl(urlRepository, urlShortenerService, maxCollisionAttempts)

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

        every { urlRepository.get(longUrl) } returns null
        every { urlShortenerService.shortIt(longUrl) } returns expectedShortUrl
        every { urlRepository.save(match { it.longUrl == longUrl && it.shortUrl == expectedShortUrl }) } returns
            expectedUrlEntity

        // When
        val result = urlService.save(longUrl)

        // Then
        expectThat(result).isEqualTo(expectedUrlEntity)
        verify(exactly = 1) {
            urlRepository.get(longUrl)
            urlShortenerService.shortIt(longUrl)
            urlRepository.save(any())
        }
    }

    @Test
    fun `should return existent url if is already present`() {
        // Given
        val longUrl = LongUrl("https://www.long.com")
        val expectedShortUrl = ShortUrl("shortUrl")
        val expectedUrlEntity = UrlEntity(randomUrlId(), expectedShortUrl, longUrl)

        every { urlRepository.get(longUrl) } returns expectedUrlEntity

        // When
        val result = urlService.save(longUrl)

        // Then
        expectThat(result).isEqualTo(expectedUrlEntity)
        verify(exactly = 1) { urlRepository.get(longUrl) }
    }

    @Test
    fun `should retry in case of a collision and persist a longUrl`() {
        // Given
        val longUrl = LongUrl("https://www.long.com")
        val shortUrl1 = ShortUrl("shortUrl1")
        val shortUrl2 = ShortUrl("shortUrl2")
        val expectedUrlEntity = UrlEntity(randomUrlId(), shortUrl2, longUrl)

        every { urlRepository.get(longUrl) } returns null
        every { urlShortenerService.shortIt(longUrl) } returnsMany listOf(shortUrl1, shortUrl2)
        every { urlRepository.save(match { it.longUrl == longUrl && it.shortUrl == shortUrl1 }) }
            .throws(DuplicateUrlException(longUrl, shortUrl1))
        every { urlRepository.save(match { it.longUrl == longUrl && it.shortUrl == shortUrl2 }) }
            .returns(expectedUrlEntity)

        // When
        val result = urlService.save(longUrl)

        // Then
        expectThat(result).isEqualTo(expectedUrlEntity)
        verify(exactly = 2) {
            urlRepository.get(longUrl)
            urlShortenerService.shortIt(longUrl)
            urlRepository.save(any())
        }
        // TODO check logs emission
    }

    @Test
    fun `should raise an IllegalStateException exceeding the maxCollisionAttempts saving a url`() {
        // Given
        val longUrl = LongUrl("https://www.long.com")
        val shortUrl1 = ShortUrl("shortUrl1")
        val shortUrl2 = ShortUrl("shortUrl2")

        every { urlRepository.get(longUrl) } returns null
        every { urlShortenerService.shortIt(longUrl) } returnsMany listOf(shortUrl1, shortUrl2)
        every { urlRepository.save(match { it.longUrl == longUrl }) } throws DuplicateUrlException(longUrl, shortUrl1)

        // When
        val exception = assertThrows<IllegalStateException> { urlService.save(longUrl) }

        // Then
        expectThat(exception.message)
            .isNotNull()
            .contains("max attempts reached")
            .contains(longUrl.value)

        verify(exactly = maxCollisionAttempts) {
            urlRepository.get(longUrl)
            urlShortenerService.shortIt(longUrl)
            urlRepository.save(any())
        }
        // TODO check logs emission
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
        verify(exactly = 1) { urlRepository.get(any<ShortUrl>()) }
    }

    private fun randomUrlId() = UrlId(nextInt(1, 100))
}
