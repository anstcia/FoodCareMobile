package com.example.foodcare.domain.usecase

import com.example.foodcare.domain.repository.ProductRepository
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String) = productRepository.deleteUserProducts(productId = productId )
}