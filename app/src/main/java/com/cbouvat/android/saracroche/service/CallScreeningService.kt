package com.cbouvat.android.saracroche.service

import android.telecom.Call
import android.telecom.CallScreeningService
import com.cbouvat.android.saracroche.util.BlockedPrefixManager

/**
 * Simple call screening service that blocks calls based on predefined prefixes
 */
class CallScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart

        if (phoneNumber != null && shouldBlockNumber(phoneNumber)) {
            // Block the call
            respondToCall(
                callDetails,
                CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipCallLog(false)
                    .setSkipNotification(false)
                    .build()
            )
        } else {
            // Allow the call
            respondToCall(
                callDetails,
                CallResponse.Builder()
                    .setDisallowCall(false)
                    .setRejectCall(false)
                    .build()
            )
        }
    }

    /**
     * Check if a phone number should be blocked based on blocked prefixes
     */
    private fun shouldBlockNumber(phoneNumber: String): Boolean {
        val blockedPrefixes = BlockedPrefixManager.getBlockedPrefixes(this)
        val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")

        return blockedPrefixes.any { prefix ->
            matchesPattern(cleanNumber, prefix.pattern)
        }
    }

    /**
     * Check if a number matches a pattern where # represents any digit
     */
    private fun matchesPattern(number: String, pattern: String): Boolean {
        if (number.length != pattern.length) return false

        for (i in pattern.indices) {
            when (pattern[i]) {
                '#' -> {
                    // # matches any digit
                    if (!number[i].isDigit()) return false
                }

                else -> {
                    // Exact character match required
                    if (number[i] != pattern[i]) return false
                }
            }
        }
        return true
    }
}
