
package com.example.foodcare.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.foodcare.api.ApiService
import com.example.foodcare.api.RetrofitClient

class RecipesViewModelFactory(
    private val userPreferences: UserPreferences,
    private val apiService: ApiService = RetrofitClient.api // Использование API-сервиса из Retrofit
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipesViewModel::class.java)) {
            return RecipesViewModel(apiService, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
