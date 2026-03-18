package com.team2.meetspace.data.repositories

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.team2.meetspace.data.entities.UserContact
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserContactRepository @Inject constructor(
    private val context: Context
) {
    fun retrieve(): List<UserContact> {
        val list = mutableListOf<UserContact>()
        try {
            context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                Log.d("Contacts", "Found ${cursor.count} contacts")
                while (cursor.moveToNext()) {
                    val name = if (nameIndex >= 0) cursor.getString(nameIndex) else null
                    val phone = if (numberIndex >= 0) cursor.getString(numberIndex) else null
                    Log.d("Contacts", "Contact: $name, $phone")
                    if (!phone.isNullOrBlank()) {
                        list.add(UserContact(name, phone))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Contacts", "Error loading contacts", e)
        }
        return list.distinctBy { it.phone }
    }
}