package com.caloshape.app.i18n

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LanguageManagerTest {

    @Test
    fun `new language tags normalize to their supported canonical tags`() {
        val expectedTags = mapOf(
            "it-IT" to "it",
            "pl-PL" to "pl",
            "vi-VN" to "vi",
            "th-TH" to "th",
            "nl-BE" to "nl",
            "sv-SE" to "sv",
            "fi-FI" to "fi",
            "id-ID" to "id",
            "in-ID" to "id",
            "hi-IN" to "hi",
            "es-MX" to "es-MX",
            "he-IL" to "he",
            "iw-IL" to "he",
            "tr-TR" to "tr",
            "ar-SA" to "ar"
        )

        expectedTags.forEach { (input, expected) ->
            assertEquals(expected, LanguageManager.normalizeTag(input))
            assertTrue(LanguageManager.isSupported(input))
        }
    }
}
