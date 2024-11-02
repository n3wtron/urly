package org.igor.urly.domain.models

import org.apache.commons.validator.routines.UrlValidator

private val urlValidator = UrlValidator(arrayOf("http", "https"))

/**
 * Value object that represent a long url
 * @throws IllegalArgumentException for invalid values
 */
data class LongUrl(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "an url cannot be blank" }
        require(urlValidator.isValid(value)) { "invalid url" }
    }
}
