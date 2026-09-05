package com.example.callblocker.helper

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log

object ContactHelper {
    fun isNumberInContacts(context: Context, phoneNumber: String): Boolean {
        if (phoneNumber.isBlank()) return false
        
        try {
            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )
            
            val projection = arrayOf(ContactsContract.PhoneLookup._ID)
            
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    return true
                }
            }
        } catch (e: SecurityException) {
            Log.e("ContactHelper", "Permission READ_CONTACTS not granted", e)
        } catch (e: Exception) {
            Log.e("ContactHelper", "Error querying contacts", e)
        }
        return false
    }

    fun getContactName(context: Context, phoneNumber: String): String? {
        if (phoneNumber.isBlank()) return null
        
        try {
            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )
            
            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
            
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        return cursor.getString(nameIndex)
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("ContactHelper", "Permission READ_CONTACTS not granted", e)
        } catch (e: Exception) {
            Log.e("ContactHelper", "Error querying contacts for name", e)
        }
        return null
    }
}
