package com.team2.meetspace.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("meetspace_prefs", Context.MODE_PRIVATE)

    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setOnboardingCompleted() {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
    }

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}