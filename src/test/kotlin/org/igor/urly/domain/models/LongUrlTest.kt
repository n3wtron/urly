package org.igor.urly.domain.models

import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class LongUrlTest {
    @ParameterizedTest
    @ValueSource(
        strings = [
            "http://www.test.com",
            "https://www.test.com",
            "https://subdomain.test.com",
            "https://www.test.com/subcontext",
            "http://www.test.com/subcontext?param=123",
        ],
    )
    fun `should create a valid longUrl`(value: String) {
        LongUrl(value)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "",
            "    ",
            "http//invalidformat.test.com",
            "ftp://invalidschema.test.com",
        ],
    )
    fun `should raise an IllegalArgumentException creating an invalid longUrl`(value: String) {
        assertThrows<IllegalArgumentException> { LongUrl(value) }
    }
}
