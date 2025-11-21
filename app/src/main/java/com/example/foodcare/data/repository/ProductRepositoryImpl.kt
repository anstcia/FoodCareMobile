package com.example.foodcare.data.repository

import com.example.foodcare.api.ApiService
import com.example.foodcare.data.mapper.toDomain
import com.example.foodcare.domain.entity.UserProduct
import com.example.foodcare.domain.repository.ProductRepository
import com.example.foodcare.presentation.viewmodel.UserPreferences
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val userPreferences: UserPreferences
) : ProductRepository {
    override suspend fun getUserProducts(): List<UserProduct> = try {
        val userId = userPreferences.getUserId()
            ?: throw IllegalStateException("User ID not found. Please login first.")
        api.getAllProductsOfUser(userId).map { it.toDomain() }
    } catch (e: IllegalStateException) {
        // Пробрасываем ошибку авторизации как есть
        throw e
    } catch (e: Exception) {
        throw RuntimeException("Failed to fetch products: ${e.message}", e)
    }
}