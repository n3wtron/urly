package org.igor.urly.domain.models

/**
 * Value object that represent a short url
 * @throws IllegalArgumentException for invalid values
 */
data class ShortUrl(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "an url cannot be blank" }
    }
}
