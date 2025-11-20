package com.example.foodcare.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodcare.domain.entity.UserProduct
import com.example.foodcare.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    init {
        loadProducts()
    }


    private val _state = MutableStateFlow<ProductState>(ProductState.Loading)
    val state: StateFlow<ProductState> = _state.asStateFlow()

    fun loadProducts() {
        viewModelScope.launch {
            try {
                val products = getProductsUseCase()
                _state.update { ProductState.Success(products) }
            } catch (e: Exception) {
                _state.update { ProductState.Error(e.message ?: "Unknown error") }
            }
        }
    }
}

sealed interface ProductState {
    object Loading : ProductState
    data class Success(val products: List<UserProduct>) : ProductState
    data class Error(val message: String) : ProductState
}
