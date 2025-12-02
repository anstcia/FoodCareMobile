package com.example.foodcare.presentation.viewmodel;

import com.example.foodcare.api.RecipeResponse;

sealed class  BarcodeState {
    object Idle : BarcodeState()
    object Loading : BarcodeState()
    data class Success(val message: String) : BarcodeState()
    data class Error(val message: String) : BarcodeState()
}
