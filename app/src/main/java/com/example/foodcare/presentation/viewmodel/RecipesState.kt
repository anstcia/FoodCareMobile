package com.example.foodcare.presentation.viewmodel

import com.example.foodcare.api.RecipeResponse

sealed class RecipesState {
    object Idle : RecipesState()
    object Loading : RecipesState()
    data class Success(val recipes: List<RecipeResponse>) : RecipesState()
    data class Error(val message: String) : RecipesState()
}
