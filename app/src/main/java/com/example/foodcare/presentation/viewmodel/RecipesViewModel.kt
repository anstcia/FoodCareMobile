package com.example.foodcare.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.identity.util.UUID
import com.example.foodcare.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecipesViewModel(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _recipesState = MutableStateFlow<RecipesState>(RecipesState.Idle)
    val recipesState: StateFlow<RecipesState> = _recipesState.asStateFlow()

    private val cachedRecipes = mutableListOf<String>()

    fun getCachedRecipes(): List<String> = cachedRecipes.toList()

    fun generateRecipes(userUuid: UUID) {
        Log.d("pipiska", "userUuid = $userUuid")
        viewModelScope.launch {
            _recipesState.value = RecipesState.Loading
            try {
                val response = apiService.generateRecipes(userUuid)
                if (response.isSuccessful) {
                    val body = response.body() ?: ""
                    cachedRecipes.clear()
                    cachedRecipes.add(body)
                    _recipesState.value = RecipesState.Success(cachedRecipes.toList())
                } else {
                    _recipesState.value = RecipesState.Error("Backend error: ${response.code()}")
                }
            } catch (e: Exception) {
                _recipesState.value = RecipesState.Error(e.message ?: "Unknown error")
            }
        }
    }


}

