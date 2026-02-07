package com.cbouvat.android.saracroche.util

import android.content.Context
import android.telephony.TelephonyManager
import android.util.Log
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.Locale

/**
 * Phone number helpers extracted from CallScreeningService to improve readability and reuse.
 */
object PhoneNumberUtils {

    private const val TAG = "PhoneNumberUtils"

    /**
     * Build a list of default region candidates for libphonenumber parsing.
     *
     * Order:
     * - network country ISO (when available)
     * - SIM country ISO
     * - default locale country
     *
     * Returned ISO codes are uppercased and distinct, with blanks removed.
     */
    fun getDefaultRegionCandidates(context: Context): List<String> {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

        val networkIso = telephonyManager?.networkCountryIso?.trim().orEmpty().uppercase()
        val simIso = telephonyManager?.simCountryIso?.trim().orEmpty().uppercase()
        val localeIso =
            runCatching { Locale.getDefault().country.trim() }.getOrDefault("").uppercase()

        val candidates = listOf(networkIso, simIso, localeIso)
            .filter { it.isNotBlank() }
            .distinct()

        Log.d(
            TAG,
            "libphonenumber region candidates: networkIso=$networkIso simIso=$simIso localeIso=$localeIso -> candidates=$candidates"
        )

        return candidates
    }

    /**
     * Normalize a phone number to E.164 using libphonenumber.
     * Returns null if parsing/validation fails.
     */
    fun toE164OrNull(raw: String, regionCandidates: List<String>): String? {
        val trimmed = raw.trim()
        val phoneNumberUtil = PhoneNumberUtil.getInstance()

        // If the number is already in international format (+CC...), the country code is explicit.
        // Using "ZZ" (unknown region) avoids any dependence on the device default region.
        if (trimmed.startsWith("+")) {
            return try {
                val parsed = phoneNumberUtil.parse(trimmed, "ZZ")

                if (!phoneNumberUtil.isValidNumber(parsed)) {
                    Log.d(TAG, "libphonenumber: invalid number for raw=$raw region=ZZ")
                    null
                } else {
                    val e164 =
                        phoneNumberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164)
                    Log.d(TAG, "libphonenumber: raw=$raw region=ZZ -> e164=$e164")
                    e164
                }
            } catch (exception: Exception) {
                Log.d(
                    TAG,
                    "libphonenumber: parse failed for raw=$raw region=ZZ error=${exception.message}"
                )
                null
            }
        }

        // For national numbers, try each candidate region until one validates.
        for (region in regionCandidates) {
            val e164 = try {
                val parsed = phoneNumberUtil.parse(trimmed, region)
                if (!phoneNumberUtil.isValidNumber(parsed)) {
                    Log.d(TAG, "libphonenumber: invalid number for raw=$raw region=$region")
                    null
                } else {
                    val formatted =
                        phoneNumberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164)
                    Log.d(TAG, "libphonenumber: raw=$raw region=$region -> e164=$formatted")
                    formatted
                }
            } catch (exception: Exception) {
                Log.d(
                    TAG,
                    "libphonenumber: parse failed for raw=$raw region=$region error=${exception.message}"
                )
                null
            }

            if (!e164.isNullOrBlank()) return e164
        }

        Log.d(TAG, "libphonenumber: cannot parse raw=$raw with candidates=$regionCandidates")
        return null
    }
}
