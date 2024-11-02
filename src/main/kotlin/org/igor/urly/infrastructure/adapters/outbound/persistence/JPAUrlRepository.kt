package org.igor.urly.infrastructure.adapters.outbound.persistence

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.igor.urly.application.ports.outbound.persistence.DuplicateUrlException
import org.igor.urly.application.ports.outbound.persistence.UrlRepository
import org.igor.urly.domain.entities.NewUrlEntity
import org.igor.urly.domain.entities.UrlEntity
import org.igor.urly.domain.models.LongUrl
import org.igor.urly.domain.models.ShortUrl
import org.igor.urly.domain.models.UrlId
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Entity(name = "urls")
class UrlRecord(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Int = 0,
    val shortUrl: String,
    var longUrl: String,
) {
    companion object {
        fun from(url: NewUrlEntity) = UrlRecord(shortUrl = url.shortUrl.value, longUrl = url.longUrl.value)
    }

    fun toEntity() = UrlEntity(UrlId(id), ShortUrl(shortUrl), LongUrl(longUrl))
}

@Repository
interface CrudJPAUrlRepository : CrudRepository<UrlRecord, Int> {
    fun findByShortUrl(url: String): UrlRecord?

    fun findByLongUrl(url: String): UrlRecord?
}

@Repository
class JPAUrlRepository(
    private val crudJPAUrlRepository: CrudJPAUrlRepository,
) : UrlRepository {
    private val logger = LoggerFactory.getLogger(JPAUrlRepository::class.java)

    override fun save(newUrl: NewUrlEntity): UrlEntity {
        val record = UrlRecord.from(newUrl)
        try {
            return crudJPAUrlRepository.save(record).toEntity()
        } catch (e: DataIntegrityViolationException) {
            logger.error("Duplicate Integrity Violation", e)
            throw DuplicateUrlException(newUrl.longUrl, newUrl.shortUrl)
        }
    }

    override fun get(shortUrl: ShortUrl) = crudJPAUrlRepository.findByShortUrl(shortUrl.value)?.toEntity()

    override fun get(longUrl: LongUrl) = crudJPAUrlRepository.findByLongUrl(longUrl.value)?.toEntity()
}
