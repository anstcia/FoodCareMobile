package com.example.foodcare.domain.usecase

import com.example.foodcare.domain.entity.UserProduct
import com.example.foodcare.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository)
{
    suspend operator fun invoke(): List<UserProduct> = productRepository.getUserProducts()
}
