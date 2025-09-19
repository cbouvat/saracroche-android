package com.cbouvat.android.saracroche

import com.cbouvat.android.saracroche.util.BlockedPattern
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CallScreeningLogicTest {

    /**
     * Test pattern matching logic
     */
    @Test
    fun testPatternMatching() {
        // Test exact match
        assertTrue(matchesPattern("33162123456", "33162######"))
        assertFalse(matchesPattern("33163123456", "33162######"))

        // Test different length
        assertFalse(matchesPattern("3316212345", "33162######"))
        assertFalse(matchesPattern("331621234567", "33162######"))

        // Test with non-digits
        assertFalse(matchesPattern("33162abcdef", "33162######"))

        // Test partial pattern
        assertTrue(matchesPattern("3394751234", "339475####"))
        assertFalse(matchesPattern("3394761234", "339475####"))
    }

    /**
     * Test number cleaning
     */
    @Test
    fun testNumberCleaning() {
        assertEquals("+33162123456", cleanNumber("+33162123456"))
        assertEquals("33162123456", cleanNumber("33 1 62 12 34 56"))
        assertEquals("33162123456", cleanNumber("33-162-123-456"))
        assertEquals("33162123456", cleanNumber("(33) 162 123 456"))
    }

    /**
     * Test should block logic with common blocked patterns
     */
    @Test
    fun testShouldBlockNumber() {
        val blockedPatterns = listOf(
            BlockedPattern("ARCEP", "33162######")
        )

        // Basic test - should block
        assertTrue("Should block 33162123456", shouldBlockNumber("33162123456", blockedPatterns))

        // Basic test - should not block
        assertFalse(
            "Should not block 33161123456",
            shouldBlockNumber("33161123456", blockedPatterns)
        )
    }

    // Helper methods that mirror the service logic
    private fun matchesPattern(number: String, pattern: String): Boolean {
        if (number.length != pattern.length) return false

        for (i in pattern.indices) {
            when (pattern[i]) {
                '#' -> {
                    if (!number[i].isDigit()) return false
                }

                else -> {
                    if (number[i] != pattern[i]) return false
                }
            }
        }
        return true
    }

    private fun cleanNumber(phoneNumber: String): String {
        return phoneNumber.replace(Regex("[^0-9+]"), "")
    }

    private fun shouldBlockNumber(
        phoneNumber: String,
        blockedPatterns: List<BlockedPattern>
    ): Boolean {
        val cleanNumber = cleanNumber(phoneNumber)
        return blockedPatterns.any { pattern ->
            matchesPattern(cleanNumber, pattern.pattern)
        }
    }
}
