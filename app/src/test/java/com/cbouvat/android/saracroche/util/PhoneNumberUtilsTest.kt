package com.cbouvat.android.saracroche.util

import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Locale

class PhoneNumberUtilsTest {

    private lateinit var mockContext: Context
    private lateinit var mockTelephonyManager: TelephonyManager
    private lateinit var originalLocale: Locale

    @Before
    fun setUp() {
        mockContext = mockk()
        mockTelephonyManager = mockk()
        originalLocale = Locale.getDefault()

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
        unmockkStatic(Log::class)
    }

    @Test
    fun `getDefaultRegionCandidates returns only locale when telephonyManager is null`() {
        every { mockContext.getSystemService(Context.TELEPHONY_SERVICE) } returns null
        Locale.setDefault(Locale.FRANCE)

        val candidates = PhoneNumberUtils.getDefaultRegionCandidates(mockContext)

        assertEquals(listOf("FR"), candidates)
    }

    /**
     * All values are null (networkCountryIso, simCountryIso, and locale country)
     */
    @Test
    fun `getDefaultRegionCandidates returns empty list when all values are null or empty`() {
        every { mockContext.getSystemService(Context.TELEPHONY_SERVICE) } returns mockTelephonyManager
        every { mockTelephonyManager.networkCountryIso } returns null
        every { mockTelephonyManager.simCountryIso } returns null
        Locale.setDefault(Locale.ROOT)

        val candidates = PhoneNumberUtils.getDefaultRegionCandidates(mockContext)

        assertTrue(candidates.isEmpty())
    }

    @Test
    fun `getDefaultRegionCandidates returns only networkCountryIso when others are null`() {
        every { mockContext.getSystemService(Context.TELEPHONY_SERVICE) } returns mockTelephonyManager
        every { mockTelephonyManager.networkCountryIso } returns "de"
        every { mockTelephonyManager.simCountryIso } returns null
        Locale.setDefault(Locale.ROOT)

        val candidates = PhoneNumberUtils.getDefaultRegionCandidates(mockContext)

        assertEquals(listOf("DE"), candidates)
    }

    @Test
    fun `getDefaultRegionCandidates returns only simCountryIso when others are null`() {
        every { mockContext.getSystemService(Context.TELEPHONY_SERVICE) } returns mockTelephonyManager
        every { mockTelephonyManager.networkCountryIso } returns null
        every { mockTelephonyManager.simCountryIso } returns "es"
        Locale.setDefault(Locale.ROOT)

        val candidates = PhoneNumberUtils.getDefaultRegionCandidates(mockContext)

        assertEquals(listOf("ES"), candidates)
    }

    @Test
    fun `getDefaultRegionCandidates returns only locale when telephony values are null`() {
        every { mockContext.getSystemService(Context.TELEPHONY_SERVICE) } returns mockTelephonyManager
        every { mockTelephonyManager.networkCountryIso } returns null
        every { mockTelephonyManager.simCountryIso } returns null
        Locale.setDefault(Locale.UK)

        val candidates = PhoneNumberUtils.getDefaultRegionCandidates(mockContext)

        assertEquals(listOf("GB"), candidates)
    }

    @Test
    fun `getDefaultRegionCandidates returns all distinct values in correct order`() {
        every { mockContext.getSystemService(Context.TELEPHONY_SERVICE) } returns mockTelephonyManager
        every { mockTelephonyManager.networkCountryIso } returns "fr"
        every { mockTelephonyManager.simCountryIso } returns "be"
        Locale.setDefault(Locale.GERMANY)

        val candidates = PhoneNumberUtils.getDefaultRegionCandidates(mockContext)

        assertEquals(listOf("FR", "BE", "DE"), candidates)
    }

    @Test
    fun `getDefaultRegionCandidates returns distinct values when all are the same`() {
        every { mockContext.getSystemService(Context.TELEPHONY_SERVICE) } returns mockTelephonyManager
        every { mockTelephonyManager.networkCountryIso } returns "fr"
        every { mockTelephonyManager.simCountryIso } returns "FR"
        Locale.setDefault(Locale.FRANCE)

        val candidates = PhoneNumberUtils.getDefaultRegionCandidates(mockContext)

        assertEquals(listOf("FR"), candidates)
    }

    @Test
    fun `getDefaultRegionCandidates trims whitespace from values`() {
        every { mockContext.getSystemService(Context.TELEPHONY_SERVICE) } returns mockTelephonyManager
        every { mockTelephonyManager.networkCountryIso } returns "  fr  "
        every { mockTelephonyManager.simCountryIso } returns " be "
        Locale.setDefault(Locale.ROOT)

        val candidates = PhoneNumberUtils.getDefaultRegionCandidates(mockContext)

        assertEquals(listOf("FR", "BE"), candidates)
    }

    @Test
    fun `getDefaultRegionCandidates filters out empty strings`() {
        every { mockContext.getSystemService(Context.TELEPHONY_SERVICE) } returns mockTelephonyManager
        every { mockTelephonyManager.networkCountryIso } returns ""
        every { mockTelephonyManager.simCountryIso } returns "   "
        Locale.setDefault(Locale.ITALY)

        val candidates = PhoneNumberUtils.getDefaultRegionCandidates(mockContext)

        assertEquals(listOf("IT"), candidates)
    }

    @Test
    fun `toE164OrNull returns null for empty number with empty candidates`() {
        val result = PhoneNumberUtils.toE164OrNull("", emptyList())

        assertNull(result)
    }

    @Test
    fun `toE164OrNull returns null for national number with empty candidates`() {
        val result = PhoneNumberUtils.toE164OrNull("0199000001", emptyList())

        assertNull(result)
    }

    @Test
    fun `toE164OrNull returns E164 for international number with empty candidates`() {
        val result = PhoneNumberUtils.toE164OrNull("+33199000001", emptyList())

        assertEquals("+33199000001", result)
    }

    @Test
    fun `toE164OrNull returns null for empty number with single candidate`() {
        val result = PhoneNumberUtils.toE164OrNull("", listOf("FR"))

        assertNull(result)
    }

    @Test
    fun `toE164OrNull returns E164 for national number with single matching candidate`() {
        val result = PhoneNumberUtils.toE164OrNull("0199000001", listOf("FR"))

        assertEquals("+33199000001", result)
    }

    @Test
    fun `toE164OrNull returns null for national number with non-matching candidate`() {
        // French number format is not valid for US
        val result = PhoneNumberUtils.toE164OrNull("0199000001", listOf("US"))

        assertNull(result)
    }

    @Test
    fun `toE164OrNull returns E164 for international number with single candidate`() {
        val result = PhoneNumberUtils.toE164OrNull("+33199000001", listOf("US"))

        assertEquals("+33199000001", result)
    }

    @Test
    fun `toE164OrNull returns null for empty number with multiple candidates`() {
        val result = PhoneNumberUtils.toE164OrNull("", listOf("FR", "BE", "DE"))

        assertNull(result)
    }

    @Test
    fun `toE164OrNull returns E164 using first matching candidate`() {
        val result = PhoneNumberUtils.toE164OrNull("0199000001", listOf("FR", "BE", "DE"))

        assertEquals("+33199000001", result)
    }

    @Test
    fun `toE164OrNull returns E164 using second candidate when first does not match`() {
        // Belgian mobile number format
        val result = PhoneNumberUtils.toE164OrNull("0199000001", listOf("US", "BE", "FR"))

        assertEquals("+33199000001", result)
    }

    @Test
    fun `toE164OrNull returns E164 for international number with multiple candidates`() {
        val result = PhoneNumberUtils.toE164OrNull("+33199000001", listOf("US", "GB", "DE"))

        assertEquals("+33199000001", result)
    }

    @Test
    fun `toE164OrNull returns null for whitespace-only number`() {
        val result = PhoneNumberUtils.toE164OrNull("   ", listOf("FR"))

        assertNull(result)
    }

    @Test
    fun `toE164OrNull trims whitespace from number`() {
        val result = PhoneNumberUtils.toE164OrNull("  +33199000001  ", listOf("FR"))

        assertEquals("+33199000001", result)
    }

    @Test
    fun `toE164OrNull returns null for invalid international number`() {
        val result = PhoneNumberUtils.toE164OrNull("+999999999999999", emptyList())

        assertNull(result)
    }

    @Test
    fun `toE164OrNull parses international number with spaces`() {
        val result = PhoneNumberUtils.toE164OrNull("+33 1 99 00 00 01", emptyList())

        assertEquals("+33199000001", result)
    }

    @Test
    fun `toE164OrNull parses national number with spaces`() {
        val result = PhoneNumberUtils.toE164OrNull("01 99 00 00 01", listOf("FR"))

        assertEquals("+33199000001", result)
    }

    @Test
    fun `toE164OrNull returns null for only plus sign`() {
        val result = PhoneNumberUtils.toE164OrNull("+", emptyList())

        assertNull(result)
    }
}
