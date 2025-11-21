package com.example.foodcare.presentation.viewmodel

import android.content.Context

class UserPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Сохраняем user_id как обычную строку
    fun saveUserId(id: String) {
        prefs.edit()
            .putString("user_id", id)
            .apply()
    }

    // Получаем user_id тоже как строку
    fun getUserId(): String? {
        return prefs.getString("user_id", null)
    }

    // Очищаем user_id
    fun clearUserId() {
        prefs.edit().remove("user_id").apply()
    }
}
