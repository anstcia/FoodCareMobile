package com.example.foodcare.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.identity.util.UUID
import com.example.foodcare.api.ApiService
import com.example.foodcare.api.RecipeResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _recipesState = MutableStateFlow<RecipesState>(RecipesState.Idle)
    val recipesState: StateFlow<RecipesState> = _recipesState.asStateFlow()

    private val cachedRecipes = mutableListOf<RecipeResponse>()

    fun getCachedRecipes(): List<RecipeResponse> = cachedRecipes.toList()


    fun generateRecipes(userUuid: UUID) {
        Log.d("RecipesViewModel", "Generating recipes for user: $userUuid")
        viewModelScope.launch {
            _recipesState.value = RecipesState.Loading
            try {
                val response = apiService.generateRecipes(userUuid)
                Log.d("RecipesViewModel", "Response received: isSuccessful=${response.isSuccessful}, code=${response.code()}")

                if (response.isSuccessful) {
                    val recipesList = response.body() ?: emptyList()
                    cachedRecipes.clear()
                    cachedRecipes.addAll(recipesList)
                    _recipesState.value = RecipesState.Success(cachedRecipes.toList())
                } else {
                    val errorMessage = "Backend error: ${response.code()} - ${response.message()}"
                    Log.e("RecipesViewModel", errorMessage)
                    _recipesState.value = RecipesState.Error(errorMessage)
                }
            } catch (e: java.net.SocketTimeoutException) {
                val errorMessage = "Request timeout. The server is taking too long to respond. Please try again."
                Log.e("RecipesViewModel", "Timeout error: ${e.message}", e)
                _recipesState.value = RecipesState.Error(errorMessage)
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error occurred"
                Log.e("RecipesViewModel", "Error generating recipes: $errorMessage", e)
                _recipesState.value = RecipesState.Error(errorMessage)
            }
        }
    }




}

