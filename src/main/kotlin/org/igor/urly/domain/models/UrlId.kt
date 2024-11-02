package org.igor.urly.domain.models

data class UrlId(
    val value: Int,
) {
    init {
        require(value > 0) { "url id has to be positive" }
    }
}
