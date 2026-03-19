package com.team2.meetspace.data.entities

data class UserContact(
    val name: String? = null,
    val phone: String? = null,
    val isChecked: Boolean = false
) {
    companion object {
        public fun fromDbEntity(userContactDb: UserContactDbTuple): UserContact {
            val userContact = UserContact(userContactDb.name, userContactDb.phone, true)
            return userContact
        }
    }
    public fun toDbTuple(meetingDb: MeetingDbEntity): UserContactDbTuple{
        var tuple = UserContactDbTuple(
            id = 0,
            meetingId = meetingDb.id,
            name = name ?: "",
            phone = phone ?: ""
            )
        return tuple
    }
    val displayName: String get() = name ?: phone ?: "Без имени"
}