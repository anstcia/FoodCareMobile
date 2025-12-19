package com.example.foodcare.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import java.util.Date

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_prefs_secure"
)

class UserPreferences(private val context: Context) {

    companion object {
        private const val TAG = "UserPreferences"
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_LOGIN = stringPreferencesKey("user_login")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_TOKEN_EXPIRY = stringPreferencesKey("token_expiry") // Для хранения времени истечения
    }

    private val dataStore = context.dataStore
    private val mutex = Mutex()

    // ------------------ Безопасные Flow для чтения ------------------
    private fun <T> createSafeFlow(
        key: Preferences.Key<T>,
        defaultValue: T? = null
    ): Flow<T?> = dataStore.data
        .catch { exception ->
            Log.e(TAG, "Error reading preference for key: ${key.name}", exception)
            emit(emptyPreferences())
        }
        .map { preferences ->
            preferences[key] ?: defaultValue
        }

    val userId: Flow<String?> = createSafeFlow(KEY_USER_ID)
    val userLogin: Flow<String?> = createSafeFlow(KEY_USER_LOGIN)
    val userName: Flow<String?> = createSafeFlow(KEY_USER_NAME)
    val accessToken: Flow<String?> = createSafeFlow(KEY_ACCESS_TOKEN)
    val refreshToken: Flow<String?> = createSafeFlow(KEY_REFRESH_TOKEN)

    // ------------------ Проверка авторизации ------------------
    val isLoggedIn: Flow<Boolean> = dataStore.data
        .catch { exception ->
            Log.e(TAG, "Error checking login status", exception)
            emit(emptyPreferences())
        }
        .map { preferences ->
            val access = preferences[KEY_ACCESS_TOKEN]
            val refresh = preferences[KEY_REFRESH_TOKEN]
            !access.isNullOrEmpty() && !refresh.isNullOrEmpty()
        }

    // ------------------ Получение токенов с проверкой ------------------
    suspend fun getAccessToken(): String? = try {
        accessToken.firstOrNull()
    } catch (e: Exception) {
        Log.e(TAG, "Failed to get access token", e)
        null
    }

    suspend fun getRefreshToken(): String? = try {
        refreshToken.firstOrNull()
    } catch (e: Exception) {
        Log.e(TAG, "Failed to get refresh token", e)
        null
    }

    // ------------------ Проверка валидности токенов ------------------
    suspend fun hasValidTokens(): Boolean {
        val access = getAccessToken()
        val refresh = getRefreshToken()
        return !access.isNullOrEmpty() && !refresh.isNullOrEmpty()
    }

    // ------------------ Сохранение токенов с временной меткой ------------------
    suspend fun saveTokens(access: String, refresh: String, expiresIn: Long = 3600) {
        mutex.withLock {
            try {
                val expiryTime = System.currentTimeMillis() + (expiresIn * 1000) - 300000 // -5 минут на запас

                dataStore.edit { prefs ->
                    prefs[KEY_ACCESS_TOKEN] = access
                    prefs[KEY_REFRESH_TOKEN] = refresh
                    prefs[KEY_TOKEN_EXPIRY] = expiryTime.toString()
                }
                Log.d(TAG, "Tokens saved, expires at: ${Date(expiryTime)}")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to save tokens", e)
                throw e
            }
        }
    }

    // ------------------ Проверка истечения access token ------------------
    suspend fun isAccessTokenExpired(): Boolean {
        return try {
            val expiryStr = dataStore.data.firstOrNull()?.get(KEY_TOKEN_EXPIRY) ?: return true
            val expiryTime = expiryStr.toLongOrNull() ?: return true
            System.currentTimeMillis() >= expiryTime
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check token expiry", e)
            true
        }
    }

    // ------------------ Сохранение пользователя ------------------
    suspend fun saveUser(id: String, login: String, name: String?) {
        mutex.withLock {
            try {
                dataStore.edit { prefs ->
                    prefs[KEY_USER_ID] = id
                    prefs[KEY_USER_LOGIN] = login

                    if (name != null) {
                        prefs[KEY_USER_NAME] = name
                    } else {
                        prefs.remove(KEY_USER_NAME)
                    }
                }

                Log.d(TAG, "User saved: id=$id, login=$login, name=$name")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to save user data", e)
                throw e
            }
        }
    }

    // ------------------ Очистка ------------------
    suspend fun clearTokens() {
        mutex.withLock {
            try {
                dataStore.edit { prefs ->
                    prefs.remove(KEY_ACCESS_TOKEN)
                    prefs.remove(KEY_REFRESH_TOKEN)
                    prefs.remove(KEY_TOKEN_EXPIRY)
                }
                Log.d(TAG, "Tokens cleared")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to clear tokens", e)
                throw e
            }
        }
    }

    suspend fun clearUser() {
        mutex.withLock {
            try {
                dataStore.edit { it.clear() }
                Log.d(TAG, "All user preferences cleared")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to clear all user preferences", e)
                throw e
            }
        }
    }

    // ------------------ Комбинированные данные ------------------
    data class UserData(
        val id: String? = null,
        val login: String? = null,
        val name: String? = null,
        val accessToken: String? = null,
        val refreshToken: String? = null
    )

    val userData: Flow<UserData> = dataStore.data
        .catch { exception ->
            Log.e(TAG, "Error reading user data", exception)
            emit(emptyPreferences())
        }
        .map { preferences ->
            UserData(
                id = preferences[KEY_USER_ID],
                login = preferences[KEY_USER_LOGIN],
                name = preferences[KEY_USER_NAME],
                accessToken = preferences[KEY_ACCESS_TOKEN],
                refreshToken = preferences[KEY_REFRESH_TOKEN]
            )
        }

    suspend fun getUserDataOnce(): UserData? = try {
        val prefs = dataStore.data.firstOrNull() ?: return null
        UserData(
            id = prefs[KEY_USER_ID],
            login = prefs[KEY_USER_LOGIN],
            name = prefs[KEY_USER_NAME],
            accessToken = prefs[KEY_ACCESS_TOKEN],
            refreshToken = prefs[KEY_REFRESH_TOKEN]
        )
    } catch (e: Exception) {
        Log.e(TAG, "Failed to get user data once", e)
        null
    }
}

private fun emptyPreferences(): Preferences = androidx.datastore.preferences.core.emptyPreferences()