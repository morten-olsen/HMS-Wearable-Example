package com.fprieto.hms.wearable.net

import android.content.SharedPreferences
import com.fprieto.hms.wearable.di.PREF_KEY_BASE_URL
import javax.inject.Inject
import javax.inject.Singleton

const val PREF_KEY_AUTH_TOKEN = "auth_token"
const val PREF_KEY_USER_ID = "user_id"
const val PREF_KEY_USERNAME = "username"


@Singleton
class AuthManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun saveAuthToken(token: String) {
        sharedPreferences.edit().putString(PREF_KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(PREF_KEY_AUTH_TOKEN, null)
    }

    fun saveUserDetails(userId: String, username: String) {
        sharedPreferences.edit()
            .putString(PREF_KEY_USER_ID, userId)
            .putString(PREF_KEY_USERNAME, username)
            .apply()
    }

    fun getUsername(): String? {
        return sharedPreferences.getString(PREF_KEY_USERNAME, null)
    }

    fun saveBaseUrl(baseUrl: String) {
        sharedPreferences.edit().putString(PREF_KEY_BASE_URL, baseUrl).apply()
    }

    fun getBaseUrl(): String? {
        return sharedPreferences.getString(PREF_KEY_BASE_URL, null)
    }

    fun clearAuthData() {
        sharedPreferences.edit()
            .remove(PREF_KEY_AUTH_TOKEN)
            .remove(PREF_KEY_USER_ID)
            .remove(PREF_KEY_USERNAME)
            // Optionally clear base URL on logout, or keep it
            // .remove(PREF_KEY_BASE_URL)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return !getAuthToken().isNullOrEmpty()
    }
}
