package com.cbouvat.android.saracroche.service

import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService.CallResponse
import android.util.Log
import com.cbouvat.android.saracroche.util.BlockedPattern
import com.cbouvat.android.saracroche.util.BlockedPatternManager
import com.cbouvat.android.saracroche.util.ContactsUtils
import com.cbouvat.android.saracroche.util.PermissionUtils
import com.cbouvat.android.saracroche.util.PreferencesManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkConstructor
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class CallScreeningServiceTest {

    private lateinit var callScreeningService: CallScreeningService

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0

        mockkObject(PreferencesManager)
        mockkObject(BlockedPatternManager)
        mockkObject(PermissionUtils)
        mockkObject(ContactsUtils)
        mockkConstructor(CallResponse.Builder::class)

        // Create a spy of CallScreeningService
        callScreeningService = spyk(CallScreeningService(), recordPrivateCalls = true)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        unmockkObject(PreferencesManager)
        unmockkObject(BlockedPatternManager)
        unmockkObject(PermissionUtils)
        unmockkObject(ContactsUtils)
        unmockkConstructor(CallResponse.Builder::class)
    }

    /**
     * Scenario:
     * - Phone number: +33199000001 (valid number)
     * - Block anonymous calls: false
     * - Block unknown numbers: false
     * - Blocked patterns: empty list (no patterns to match)
     * - Expected: Call is allowed (disallowCall = false, rejectCall = false)
     */
    @Test
    fun `respondToCall is called with disallowCall false and rejectCall false when number is not blocked`() {
        val phoneNumber = "+33199000001"

        // Mock Call.Details
        val mockCallDetails = mockk<Call.Details>()
        val mockHandle = mockk<Uri>()
        every { mockCallDetails.handle } returns mockHandle
        every { mockHandle.schemeSpecificPart } returns phoneNumber

        // Mock preferences - no blocking enabled
        coEvery { PreferencesManager.getBlockAnonymousCalls(any()) } returns false
        coEvery { PreferencesManager.getBlockUnknownNumbers(any()) } returns false

        // Mock blocked patterns - empty list (no patterns to block)
        every { BlockedPatternManager.getBlockedPatterns(any()) } returns emptyList<BlockedPattern>()

        // Track the values passed to CallResponse.Builder
        val disallowCallSlot = slot<Boolean>()
        val rejectCallSlot = slot<Boolean>()
        val mockCallResponse = mockk<CallResponse>()

        every { anyConstructed<CallResponse.Builder>().setDisallowCall(capture(disallowCallSlot)) } answers { self as CallResponse.Builder }
        every { anyConstructed<CallResponse.Builder>().setRejectCall(capture(rejectCallSlot)) } answers { self as CallResponse.Builder }
        every { anyConstructed<CallResponse.Builder>().setSkipCallLog(any()) } answers { self as CallResponse.Builder }
        every { anyConstructed<CallResponse.Builder>().setSkipNotification(any()) } answers { self as CallResponse.Builder }
        every { anyConstructed<CallResponse.Builder>().build() } returns mockCallResponse
        every { callScreeningService.respondToCall(any(), any()) } returns Unit

        callScreeningService.onScreenCall(mockCallDetails)

        verify(exactly = 1) {
            callScreeningService.respondToCall(
                mockCallDetails,
                mockCallResponse
            )
        }

        assertFalse("disallowCall should be false", disallowCallSlot.captured)
        assertFalse("rejectCall should be false", rejectCallSlot.captured)
    }

    /**
     * Scenario:
     * - Phone number: +33199000001 (valid number)
     * - Block anonymous calls: false
     * - Block unknown numbers: true
     * - Number is NOT in contacts
     * - Contacts permission: granted
     * - Blocked patterns: empty list (no patterns to match)
     * - Expected: Call is blocked (disallowCall = true, rejectCall = true)
     */
    @Test
    fun `respondToCall is called with disallowCall true and rejectCall true when unknown number and blocking enabled`() {
        val phoneNumber = "+33199000001"

        // Mock Call.Details
        val mockCallDetails = mockk<Call.Details>()
        val mockHandle = mockk<Uri>()
        every { mockCallDetails.handle } returns mockHandle
        every { mockHandle.schemeSpecificPart } returns phoneNumber

        // Mock preferences - block unknown numbers enabled
        coEvery { PreferencesManager.getBlockAnonymousCalls(any()) } returns false
        coEvery { PreferencesManager.getBlockUnknownNumbers(any()) } returns true

        // Mock permissions - contacts permission granted
        every { PermissionUtils.hasContactsPermission(any()) } returns true

        // Mock contacts - number is NOT in contacts
        every { ContactsUtils.isPhoneNumberInContacts(any(), phoneNumber) } returns false

        // Track the values passed to CallResponse.Builder
        val disallowCallSlot = slot<Boolean>()
        val rejectCallSlot = slot<Boolean>()
        val mockCallResponse = mockk<CallResponse>()

        every { anyConstructed<CallResponse.Builder>().setDisallowCall(capture(disallowCallSlot)) } answers { self as CallResponse.Builder }
        every { anyConstructed<CallResponse.Builder>().setRejectCall(capture(rejectCallSlot)) } answers { self as CallResponse.Builder }
        every { anyConstructed<CallResponse.Builder>().setSkipCallLog(any()) } answers { self as CallResponse.Builder }
        every { anyConstructed<CallResponse.Builder>().setSkipNotification(any()) } answers { self as CallResponse.Builder }
        every { anyConstructed<CallResponse.Builder>().build() } returns mockCallResponse
        every { callScreeningService.respondToCall(any(), any()) } returns Unit

        callScreeningService.onScreenCall(mockCallDetails)

        verify(exactly = 1) {
            callScreeningService.respondToCall(
                mockCallDetails,
                mockCallResponse
            )
        }

        assert(disallowCallSlot.captured) { "disallowCall should be true" }
        assert(rejectCallSlot.captured) { "rejectCall should be true" }

        // Verify that BlockedPatternManager.getBlockedPatterns was NOT called
        // because shouldBlockUnknownNumber returned true before reaching the pattern check
        verify(exactly = 0) { BlockedPatternManager.getBlockedPatterns(any()) }
    }
}
