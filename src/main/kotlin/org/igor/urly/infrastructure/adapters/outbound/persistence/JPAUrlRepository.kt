package org.igor.urly.infrastructure.adapters.outbound.persistence

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.igor.urly.application.ports.outbound.persistence.UrlRepository
import org.igor.urly.domain.entities.NewUrlEntity
import org.igor.urly.domain.entities.UrlEntity
import org.igor.urly.domain.models.LongUrl
import org.igor.urly.domain.models.ShortUrl
import org.igor.urly.domain.models.UrlId
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
}

@Repository
class JPAUrlRepository(
    private val crudJPAUrlRepository: CrudJPAUrlRepository,
) : UrlRepository {
    override fun save(newUrl: NewUrlEntity) = UrlRecord.from(newUrl).let(crudJPAUrlRepository::save).toEntity()

    override fun get(shortUrl: ShortUrl) = crudJPAUrlRepository.findByShortUrl(shortUrl.value)?.toEntity()
}
