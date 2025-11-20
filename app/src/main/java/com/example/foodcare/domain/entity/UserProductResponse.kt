package com.example.foodcare.domain.entity

import com.example.foodcare.data.remote.OrderProductDto
import com.example.foodcare.data.remote.ProductDto

data class UserProductResponse(
    val order_product: OrderProductDto,
    val product: ProductDto
)
