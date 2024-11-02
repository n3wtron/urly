package org.igor.urly.domain.models

import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ShortUrlTest {
    @ParameterizedTest
    @ValueSource(strings = ["valid", "a"])
    fun `should create a valid shortUrl`(value: String) {
        ShortUrl(value)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "    "])
    fun `should raise an IllegalArgumentException creating an invalid shortUrl`(value: String) {
        assertThrows<IllegalArgumentException> { ShortUrl(value) }
    }
}
