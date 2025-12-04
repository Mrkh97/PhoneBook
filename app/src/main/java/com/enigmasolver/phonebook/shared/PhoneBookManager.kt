package com.enigmasolver.phonebook.shared

import android.content.ContentProviderOperation
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhoneBookManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun contactExists(phoneNumber: String): Boolean {
        if (phoneNumber.isBlank()) return false

        return withContext(Dispatchers.IO) {
            if (context.checkSelfPermission(android.Manifest.permission.READ_CONTACTS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                return@withContext false
            }

            val lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )

            val projection = arrayOf(ContactsContract.PhoneLookup._ID)

            try {
                context.contentResolver.query(lookupUri, projection, null, null, null)
                    ?.use { cursor ->
                        return@use cursor.count > 0
                    } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun saveContact(firstName: String, lastName: String, phoneNumber: String): Boolean {
        return withContext(Dispatchers.IO) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_CONTACTS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                return@withContext false
            }

            val ops = ArrayList<ContentProviderOperation>()

            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build()
            )
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                        firstName
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                        lastName
                    )
                    .build()
            )
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    )
                    .build()
            )

            try {
                context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}