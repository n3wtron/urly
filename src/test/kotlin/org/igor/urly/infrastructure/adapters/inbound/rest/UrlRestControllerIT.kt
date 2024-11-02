package org.igor.urly.infrastructure.adapters.inbound.rest

import org.igor.urly.application.ports.outbound.persistence.UrlRepository
import org.igor.urly.domain.entities.NewUrlEntity
import org.igor.urly.domain.models.LongUrl
import org.igor.urly.domain.models.ShortUrl
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

@SpringBootTest
@ActiveProfiles("integration-test")
@AutoConfigureMockMvc
class UrlRestControllerIT {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var urlRepository: UrlRepository

    @Test
    fun `should persist a new url`() {
        val longUrl = "http://www.long.com"
        val result =
            mockMvc
                .post("/urls") {
                    this.contentType = MediaType.APPLICATION_JSON
                    this.content =
                        """
                        { 
                            "url":"$longUrl"
                        }
                        """.trimIndent()
                }.andExpect {
                    status { isOk() }
                    content {
                        contentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                    }
                }.andReturn()

        val shortUrlResponse = result.response.contentAsString
        val record = urlRepository.get(ShortUrl(shortUrlResponse))
        expectThat(record)
            .isNotNull()
            .and {
                get { this.longUrl.value }.isEqualTo(longUrl)
                get { this.shortUrl.value }.isEqualTo(shortUrlResponse)
            }
    }

    @Test
    fun `should retrieve a longUrl from a shortUrl`() {
        // Given
        // TODO: randomize values
        val longUrl = LongUrl("http://www.looong2.com")
        val shortUrl = ShortUrl("blblblb")
        urlRepository.save(NewUrlEntity(shortUrl, longUrl))

        // When
        val result =
            mockMvc
                .get("/urls?url={shortUrl}", shortUrl.value)
                .andExpect {
                    status { isOk() }
                    content {
                        contentTypeCompatibleWith(MediaType.TEXT_PLAIN)
                    }
                }.andReturn()

        // Then
        expectThat(result.response.contentAsString).isEqualTo(longUrl.value)
    }

    @Test
    fun `should return 404 when the shortUrl is not present`() {
        // Given
        // TODO: randomize values
        val shortUrl = ShortUrl("notexist")

        mockMvc
            .get("/urls?url={shortUrl}", shortUrl.value)
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `should return 400 when the long url is invalid`() {
        // TODO: write me
    }
}
