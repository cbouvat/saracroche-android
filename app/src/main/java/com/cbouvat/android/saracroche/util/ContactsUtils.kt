package com.cbouvat.android.saracroche.util

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log

/**
 * Contacts lookup helpers.
 *
 * Used to determine whether an incoming phone number exists in the user contacts.
 */
object ContactsUtils {

    private const val TAG = "ContactsUtils"

    fun isPhoneNumberInContacts(context: Context, phoneNumber: String): Boolean {
        val candidate = sanitizePhoneNumberForLookup(phoneNumber)

        return if (isInContactsViaPhoneLookup(
                context,
                candidate
            )
        ) true else isInContactsViaE164Fallback(context, candidate)
    }

    /**
     * Basic cleanup before lookup.
     *
     * This is intentionally minimal and mirrors the previous behavior:
     * remove spaces, hyphens and parentheses.
     */
    private fun sanitizePhoneNumberForLookup(phoneNumber: String): String {
        return phoneNumber
            .replace(" ", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
    }

    private fun isInContactsViaPhoneLookup(context: Context, candidate: String): Boolean {
        val lookupProjection = arrayOf(ContactsContract.PhoneLookup._ID)
        val lookupUri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(candidate)
        )


        return try {
            context.contentResolver.query(
                lookupUri,
                lookupProjection,
                null,
                null,
                null
            )?.use { cursor ->
                cursor.moveToFirst()
            } ?: false
        } catch (exception: Exception) {
            Log.e(TAG, "Error during contacts lookup", exception)
            false
        }
    }

    private fun isInContactsViaE164Fallback(context: Context, candidate: String): Boolean {
        val regionCandidates = PhoneNumberUtils.getDefaultRegionCandidates(context)
        val candidateE164 = PhoneNumberUtils.toE164OrNull(candidate, regionCandidates)

        if (candidateE164.isNullOrBlank()) {
            return false
        }

        val phonesUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val phonesProjection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        return try {
            context.contentResolver.query(phonesUri, phonesProjection, null, null, null)
                ?.use { cursor ->
                    val numberIdx =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    if (numberIdx < 0) return@use false

                    while (cursor.moveToNext()) {
                        val contactNumber = cursor.getString(numberIdx) ?: continue
                        val contactE164 =
                            PhoneNumberUtils.toE164OrNull(contactNumber, regionCandidates)
                                ?: continue
                        if (contactE164 == candidateE164) {
                            // libphonenumber EXACT_MATCH candidateE164
                            return@use true
                        }
                    }
                    false
                } ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Error during libphonenumber fallback contact scan", e)
            false
        }
    }
}
