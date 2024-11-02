package org.igor.urly.infrastructure.adapters.outbound.persistence

import org.igor.urly.domain.entities.NewUrlEntity
import org.igor.urly.domain.models.LongUrl
import org.igor.urly.domain.models.ShortUrl
import org.igor.urly.domain.models.UrlId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

@SpringBootTest
@ActiveProfiles("integration-test")
class JPAUrlRepositoryIT {
    @Autowired
    private lateinit var crudJPAUrlRepository: CrudJPAUrlRepository

    @Autowired
    private lateinit var jpaUrlRepository: JPAUrlRepository

    @Test
    fun `should save a newEntity into DB`() {
        // Given
        // TODO: create random values
        val shortUrl = ShortUrl("abc")
        val longUrl = LongUrl("http://www.urly.com")
        val newEntity = NewUrlEntity(shortUrl, longUrl)

        // When
        jpaUrlRepository.save(newEntity)

        // Then
        val record = crudJPAUrlRepository.findByShortUrl(shortUrl.value)

        expectThat(record)
            .isNotNull()
            .and {
                get { this.longUrl }.isEqualTo(longUrl.value)
                get { this.shortUrl }.isEqualTo(shortUrl.value)
                get { this.id }.assertThat("positive") { it > 0 }
            }
    }

    @Test
    fun `should retrieve a urlEntity from DB`() {
        // Given
        // TODO: create random values
        val shortUrl = ShortUrl("abcd")
        val longUrl = LongUrl("http://www.urly2.com")
        val record = UrlRecord(shortUrl = shortUrl.value, longUrl = longUrl.value)
        val dbRecord = crudJPAUrlRepository.save(record)
        val expectedUrlId = UrlId(dbRecord.id)

        // When
        val result = jpaUrlRepository.get(shortUrl)

        // Then
        expectThat(result)
            .isNotNull()
            .and {
                get { this.longUrl }.isEqualTo(longUrl)
                get { this.shortUrl }.isEqualTo(shortUrl)
                get { this.id }.isEqualTo(expectedUrlId)
            }
    }
}
