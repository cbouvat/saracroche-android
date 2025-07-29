package com.cbouvat.android.saracroche.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.cbouvat.android.saracroche.util.BlockedPrefixManager

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
                .setSkipNotification(true)
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
     * Check if a phone number should be blocked based on blocked prefixes
     */
    private fun shouldBlockNumber(phoneNumber: String): Boolean {
        val blockedPrefixes = BlockedPrefixManager.getBlockedPrefixes(this)
        return blockedPrefixes.any { it.prefix.let(phoneNumber::startsWith) }
    }
}
