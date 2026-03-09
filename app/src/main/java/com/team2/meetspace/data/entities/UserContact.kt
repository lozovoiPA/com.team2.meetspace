package com.team2.meetspace.data.entities

data class UserContact(
    val name: String? = null,
    val phone: String? = null,
    val isChecked: Boolean = false
) {
    val displayName: String get() = name ?: phone ?: "Без имени"
}