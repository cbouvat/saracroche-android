package com.cbouvat.android.saracroche.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.cbouvat.android.saracroche.util.BlockedPatternManager
import com.cbouvat.android.saracroche.util.ContactsUtils
import com.cbouvat.android.saracroche.util.PermissionUtils
import com.cbouvat.android.saracroche.util.PreferencesManager
import kotlinx.coroutines.runBlocking

/**
 * Simple call screening service that blocks calls based on predefined prefixes
 */
class CallScreeningService : CallScreeningService() {

    private companion object {
        private const val TAG = "CallScreeningService"
    }

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart
        Log.d(TAG, "Incoming call from: $phoneNumber")

        val shouldBlock = shouldBlockNumber(phoneNumber)

        val response = if (shouldBlock) {
            Log.d(TAG, "Blocking call from: $phoneNumber")
            CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipCallLog(false)
                .setSkipNotification(false)
                .build()
        } else {
            Log.d(TAG, "Allowing call from: $phoneNumber")
            CallResponse.Builder()
                .setDisallowCall(false)
                .setRejectCall(false)
                .build()
        }

        respondToCall(callDetails, response)
    }

    /**
     * Check if a phone number should be blocked based on blocked patterns or anonymous call setting
     */
    private fun shouldBlockNumber(phoneNumber: String?): Boolean {
        // Check if this is an anonymous call (null or empty phone number)
        if (phoneNumber.isNullOrBlank()) {
            return runBlocking {
                try {
                    PreferencesManager.getBlockAnonymousCalls(this@CallScreeningService)
                } catch (e: Exception) {
                    Log.e(TAG, "Error checking anonymous call preference", e)
                    false
                }
            }
        }

        // Check blocked patterns for regular phone numbers
        val normalizedNumber = normalizePhoneNumber(phoneNumber)

        if (shouldBlockUnknownNumber(phoneNumber)) return true

        val blockedPatterns = BlockedPatternManager.getBlockedPatterns(this)

        return blockedPatterns.any { pattern ->
            matchesPattern(normalizedNumber, pattern.pattern)
        }
    }

    private fun shouldBlockUnknownNumber(phoneNumber: String): Boolean {
        // Block unknown numbers (not in contacts) if the option is enabled
        val blockUnknownNumbersEnabled = runBlocking {
            try {
                PreferencesManager.getBlockUnknownNumbers(this@CallScreeningService)
            } catch (e: Exception) {
                Log.e(TAG, "Error checking unknown numbers preference", e)
                false
            }
        }

        if (!blockUnknownNumbersEnabled) return false

        // If we don't have contacts permission, we can't reliably decide: do not block.
        if (!PermissionUtils.hasContactsPermission(this)) {
            Log.w(
                TAG,
                "Block unknown numbers enabled but contacts permission missing; allowing call."
            )
            return false
        }

        return !ContactsUtils.isPhoneNumberInContacts(this, phoneNumber)
    }

    /**
     * Normalize phone number by removing the + prefix
     */
    private fun normalizePhoneNumber(phoneNumber: String): String {
        return if (phoneNumber.startsWith("+")) {
            phoneNumber.substring(1)
        } else {
            phoneNumber
        }
    }

    /**
     * Check if a phone number matches a pattern with wildcards (#)
     * Both numbers must have the same length after normalization
     */
    private fun matchesPattern(phoneNumber: String, pattern: String): Boolean {
        if (phoneNumber.length != pattern.length) {
            return false
        }

        return pattern.indices.all { i ->
            pattern[i] == '#' || pattern[i] == phoneNumber[i]
        }
    }
}
