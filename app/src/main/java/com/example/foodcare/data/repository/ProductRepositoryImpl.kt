package com.example.foodcare.data.repository

import com.example.foodcare.api.ApiService
import com.example.foodcare.data.mapper.toDomain
import com.example.foodcare.domain.entity.UserProduct
import com.example.foodcare.domain.repository.ProductRepository
import com.example.foodcare.presentation.viewmodel.UserPreferences
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val userPreferences: UserPreferences
) : ProductRepository {
    override suspend fun getUserProducts(): List<UserProduct> = try {
        val userId =  userPreferences.userId.firstOrNull()
        //?: throw IllegalStateException("User ID not found. Please login first.")
        api.getAllProductsOfUser(userId).map { it.toDomain() }
    } catch (e: IllegalStateException) {
        // Пробрасываем ошибку авторизации как есть
        throw e
    } catch (e: Exception) {
        throw RuntimeException("Failed to fetch products: ${e.message}", e)
    }
    override suspend fun deleteUserProducts(productId: String) {
        api.deleteProductOfUser(productId = productId)
    }
}//98195f76-288f-4335-b5d6-cfd9554d4401