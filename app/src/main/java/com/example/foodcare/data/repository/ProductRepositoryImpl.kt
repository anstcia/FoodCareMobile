package com.example.foodcare.data.repository

import com.example.foodcare.api.ApiService
import com.example.foodcare.data.mapper.toDomain
import com.example.foodcare.domain.entity.UserProduct
import com.example.foodcare.domain.repository.ProductRepository

private const val USER_ID = "aad5fed6-4714-4b47-91d4-cb8b0ac885dc"
class ProductRepositoryImpl(
    private val api: ApiService
) : ProductRepository {
    override suspend fun getUserProducts(): List<UserProduct> = try {
        api.getAllProductsOfUser(USER_ID).map { it.toDomain() }
    } catch (e: Exception) {
        throw RuntimeException("Failed to fetch products", e)
    }
}