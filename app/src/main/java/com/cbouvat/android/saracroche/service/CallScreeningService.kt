package com.cbouvat.android.saracroche.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.cbouvat.android.saracroche.util.BlockedPatternManager

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

        val shouldBlock = phoneNumber?.let { shouldBlockNumber(it) } ?: false

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
     * Check if a phone number should be blocked based on blocked patterns
     * Removes the + prefix if present before checking patterns
     */
    private fun shouldBlockNumber(phoneNumber: String): Boolean {
        val normalizedNumber = normalizePhoneNumber(phoneNumber)
        val blockedPatterns = BlockedPatternManager.getBlockedPatterns(this)
        
        return blockedPatterns.any { pattern ->
            matchesPattern(normalizedNumber, pattern.pattern)
        }
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
