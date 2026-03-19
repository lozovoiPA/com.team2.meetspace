package com.team2.meetspace.data.repositories

import android.util.Log
import com.team2.meetspace.data.dataSources.UserContactLocalDataSource
import com.team2.meetspace.data.entities.UserContact

class UserContactRepository (private val dataSource: UserContactLocalDataSource) {
    fun retrieve(): List<UserContact> {
        try {
            val list = dataSource.retrieve();
            return list.distinctBy { it.phone }
        } catch (e: Exception) {
            Log.e("Error", "Error loading contacts", e)
        }
        return emptyList();
    }
}