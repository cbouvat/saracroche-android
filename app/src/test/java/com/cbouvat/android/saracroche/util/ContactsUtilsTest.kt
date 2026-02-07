package com.cbouvat.android.saracroche.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Locale

class ContactsUtilsTest {

    private lateinit var mockContext: Context
    private lateinit var mockContentResolver: ContentResolver

    @Before
    fun setUp() {
        mockContext = mockk()
        mockContentResolver = mockk()

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        // Mock Uri.withAppendedPath and Uri.encode (static methods)
        mockkStatic(Uri::class)

        // Setup context to return our mock ContentResolver
        every { mockContext.contentResolver } returns mockContentResolver
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        unmockkStatic(Uri::class)
    }

    @Test
    fun `isPhoneNumberInContacts returns true when number found via PhoneLookup`() {
        val phoneNumber = "+33199000001"
        val mockUri = mockk<Uri>()
        val mockCursor = mockk<Cursor>()

        // Mock Uri static methods
        every { Uri.encode(any()) } returns phoneNumber
        every { Uri.withAppendedPath(any(), any()) } returns mockUri

        // Mock the cursor behavior:
        // - moveToFirst() returns true (found a contact)
        // - close() is called by .use {}
        every { mockCursor.moveToFirst() } returns true
        every { mockCursor.close() } returns Unit
        every {
            mockContentResolver.query(
                mockUri,
                any(),
                isNull(),
                isNull(),
                isNull()
            )
        } returns mockCursor

        val result = ContactsUtils.isPhoneNumberInContacts(mockContext, phoneNumber)

        assertTrue("Phone number should be found in contacts", result)
    }

    /**
     * This tests the fallback path where:
     * - PhoneLookup returns no result (cursor.moveToFirst() = false)
     * - E164 fallback scans all contacts and finds a match
     */
    @Test
    fun `isPhoneNumberInContacts returns true via E164 fallback when PhoneLookup fails`() {
        val phoneNumber = "+33199000001"
        val contactNumber = "01 99 00 00 01" // Same number in national format with spaces

        // Mock Uri static methods
        val mockPhoneLookupUri = mockk<Uri>()
        every { Uri.encode(any()) } returns phoneNumber
        every { Uri.withAppendedPath(any(), any()) } returns mockPhoneLookupUri

        // First query: PhoneLookup returns no result
        val mockPhoneLookupCursor = mockk<Cursor>()
        every { mockPhoneLookupCursor.moveToFirst() } returns false
        every { mockPhoneLookupCursor.close() } returns Unit

        every {
            mockContentResolver.query(
                mockPhoneLookupUri,
                any(),
                isNull(),
                isNull(),
                isNull()
            )
        } returns mockPhoneLookupCursor

        // Mock TelephonyManager for getDefaultRegionCandidates
        val mockTelephonyManager = mockk<TelephonyManager>()
        every { mockContext.getSystemService(Context.TELEPHONY_SERVICE) } returns mockTelephonyManager
        every { mockTelephonyManager.networkCountryIso } returns "fr"
        every { mockTelephonyManager.simCountryIso } returns "fr"

        // Second query: E164 fallback scans all contacts
        val mockContactsCursor = mockk<Cursor>()
        val numberColumnIndex = 0

        every { mockContactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) } returns numberColumnIndex
        every { mockContactsCursor.moveToNext() } returnsMany listOf(true, false)
        every { mockContactsCursor.getString(numberColumnIndex) } returns contactNumber
        every { mockContactsCursor.close() } returns Unit

        every {
            mockContentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                any(),
                isNull(),
                isNull(),
                isNull()
            )
        } returns mockContactsCursor

        val result = ContactsUtils.isPhoneNumberInContacts(mockContext, phoneNumber)

        assertTrue("Phone number should be found via E164 fallback", result)
    }

    /**
     * This tests the case where:
     * - PhoneLookup returns no result
     * - E164 fallback cannot parse the number because regionCandidates is empty
     * - The number is in national format (no + prefix), so it cannot be parsed without a region
     */
    @Test
    fun `isPhoneNumberInContacts returns false when regionCandidates is empty`() {
        val phoneNumber = "0199000001" // National format, needs region to parse

        // Mock Uri static methods
        val mockPhoneLookupUri = mockk<Uri>()
        every { Uri.encode(any()) } returns phoneNumber
        every { Uri.withAppendedPath(any(), any()) } returns mockPhoneLookupUri

        // First query: PhoneLookup returns no result
        val mockPhoneLookupCursor = mockk<Cursor>()
        every { mockPhoneLookupCursor.moveToFirst() } returns false
        every { mockPhoneLookupCursor.close() } returns Unit

        every {
            mockContentResolver.query(
                mockPhoneLookupUri,
                any(),
                isNull(),
                isNull(),
                isNull()
            )
        } returns mockPhoneLookupCursor

        // Mock TelephonyManager to return empty regionCandidates
        val mockTelephonyManager = mockk<TelephonyManager>()
        every { mockContext.getSystemService(Context.TELEPHONY_SERVICE) } returns mockTelephonyManager
        every { mockTelephonyManager.networkCountryIso } returns null
        every { mockTelephonyManager.simCountryIso } returns null

        // Set Locale to ROOT so locale country is also empty
        val originalLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.ROOT)

            val result = ContactsUtils.isPhoneNumberInContacts(mockContext, phoneNumber)

            assertFalse("Phone number should not be found when regionCandidates is empty", result)
        } finally {
            Locale.setDefault(originalLocale)
        }
    }

    /**
     * This tests the case where:
     * - PhoneLookup returns no result
     * - E164 fallback scans all contacts but finds no match
     */
    @Test
    fun `isPhoneNumberInContacts returns false when number not found in contacts`() {
        val phoneNumber = "+33199000001"
        val contactNumber1 = "+33199000002" // Different number
        val contactNumber2 = "+33199000003" // Another different number

        val mockPhoneLookupUri = mockk<Uri>()
        every { Uri.encode(any()) } returns phoneNumber
        every { Uri.withAppendedPath(any(), any()) } returns mockPhoneLookupUri

        // First query: PhoneLookup returns no result
        val mockPhoneLookupCursor = mockk<Cursor>()
        every { mockPhoneLookupCursor.moveToFirst() } returns false
        every { mockPhoneLookupCursor.close() } returns Unit

        every {
            mockContentResolver.query(
                mockPhoneLookupUri,
                any(),
                isNull(),
                isNull(),
                isNull()
            )
        } returns mockPhoneLookupCursor

        // Mock TelephonyManager for getDefaultRegionCandidates
        val mockTelephonyManager = mockk<TelephonyManager>()
        every { mockContext.getSystemService(Context.TELEPHONY_SERVICE) } returns mockTelephonyManager
        every { mockTelephonyManager.networkCountryIso } returns "fr"
        every { mockTelephonyManager.simCountryIso } returns "fr"

        // Second query: E164 fallback scans all contacts but no match
        val mockContactsCursor = mockk<Cursor>()
        val numberColumnIndex = 0

        every { mockContactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) } returns numberColumnIndex
        every { mockContactsCursor.moveToNext() } returnsMany listOf(true, true, false)
        every { mockContactsCursor.getString(numberColumnIndex) } returnsMany listOf(
            contactNumber1,
            contactNumber2
        )
        every { mockContactsCursor.close() } returns Unit

        every {
            mockContentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                any(),
                isNull(),
                isNull(),
                isNull()
            )
        } returns mockContactsCursor

        val result = ContactsUtils.isPhoneNumberInContacts(mockContext, phoneNumber)

        assertFalse("Phone number should not be found when not in contacts", result)
    }

    /**
     * This tests the case where:
     * - PhoneLookup returns no result
     * - E164 fallback query returns a cursor but getColumnIndex returns -1
     *   (column not found in cursor)
     */
    @Test
    fun `isPhoneNumberInContacts returns false when column index is negative`() {
        val phoneNumber = "+33199000001"

        val mockPhoneLookupUri = mockk<Uri>()
        every { Uri.encode(any()) } returns phoneNumber
        every { Uri.withAppendedPath(any(), any()) } returns mockPhoneLookupUri

        // First query: PhoneLookup returns no result
        val mockPhoneLookupCursor = mockk<Cursor>()
        every { mockPhoneLookupCursor.moveToFirst() } returns false
        every { mockPhoneLookupCursor.close() } returns Unit

        every {
            mockContentResolver.query(
                mockPhoneLookupUri,
                any(),
                isNull(),
                isNull(),
                isNull()
            )
        } returns mockPhoneLookupCursor

        // Mock TelephonyManager for getDefaultRegionCandidates
        val mockTelephonyManager = mockk<TelephonyManager>()
        every { mockContext.getSystemService(Context.TELEPHONY_SERVICE) } returns mockTelephonyManager
        every { mockTelephonyManager.networkCountryIso } returns "fr"
        every { mockTelephonyManager.simCountryIso } returns "fr"

        // Second query: E164 fallback but column index is -1
        val mockContactsCursor = mockk<Cursor>()

        // getColumnIndex returns -1 (column not found)
        every { mockContactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) } returns -1
        every { mockContactsCursor.close() } returns Unit

        every {
            mockContentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                any(),
                isNull(),
                isNull(),
                isNull()
            )
        } returns mockContactsCursor

        val result = ContactsUtils.isPhoneNumberInContacts(mockContext, phoneNumber)

        assertFalse("Phone number should not be found when column index is negative", result)
    }

    /**
     * This tests the case where:
     * - PhoneLookup throws an exception
     * - E164 fallback also throws an exception
     * - The method catches exceptions and returns false
     */
    @Test
    fun `isPhoneNumberInContacts returns false when exception is thrown`() {
        val phoneNumber = "+33199000001"

        val mockPhoneLookupUri = mockk<Uri>()
        every { Uri.encode(any()) } returns phoneNumber
        every { Uri.withAppendedPath(any(), any()) } returns mockPhoneLookupUri

        // First query: PhoneLookup throws an exception
        every {
            mockContentResolver.query(
                mockPhoneLookupUri,
                any(),
                isNull(),
                isNull(),
                isNull()
            )
        } throws SecurityException("Permission denied")

        // Mock TelephonyManager for getDefaultRegionCandidates
        val mockTelephonyManager = mockk<TelephonyManager>()
        every { mockContext.getSystemService(Context.TELEPHONY_SERVICE) } returns mockTelephonyManager
        every { mockTelephonyManager.networkCountryIso } returns "fr"
        every { mockTelephonyManager.simCountryIso } returns "fr"

        // Second query: E164 fallback also throws an exception
        every {
            mockContentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                any(),
                isNull(),
                isNull(),
                isNull()
            )
        } throws SecurityException("Permission denied")

        val result = ContactsUtils.isPhoneNumberInContacts(mockContext, phoneNumber)

        assertFalse("Phone number should not be found when exception is thrown", result)
    }
}
