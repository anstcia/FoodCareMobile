package com.example.foodcare.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodcare.domain.entity.UserProduct
import com.example.foodcare.domain.usecase.DeleteProductUseCase
import com.example.foodcare.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow<ProductState>(ProductState.Loading)
    val state: StateFlow<ProductState> = _state.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            // проверяем авторизацию перед загрузкой
            if (userPreferences.userId.firstOrNull() == null) {
                _state.update { ProductState.Error("Please login to view products") }
                return@launch
            }

            try {
                val products = getProductsUseCase()
                _state.update { ProductState.Success(products) }
            } catch (e: IllegalStateException) {
                // ошибка авторизации
                _state.update { ProductState.Error("Please login to view products") }
            } catch (e: Exception) {
                _state.update { ProductState.Error(e.message ?: "Unknown error") }
            }
        }
    }
    fun deleteProduct(product: UserProduct) {
        viewModelScope.launch {
            try {
                deleteProductUseCase(product.orderProductId)
                loadProducts()
            } catch (e: Exception) {
                _state.update {
                    ProductState.Error("Не удалось удалить продукт")
                }
            }
        }
    }


}

sealed interface ProductState {
    object Loading : ProductState
    data class Success(val products: List<UserProduct>) : ProductState
    data class Error(val message: String) : ProductState
}
